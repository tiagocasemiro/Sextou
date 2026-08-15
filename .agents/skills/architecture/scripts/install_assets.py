#!/usr/bin/env python3
"""Install reusable Kotlin assets into an Android project."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


DEFAULT_PACKAGE = "com.example.app"
PACKAGE_PATTERN = re.compile(r"^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$")
SKILL_ROOT = Path(__file__).resolve().parent.parent

DOMAIN_ASSETS = (
    Path("assets/usecase/domain/Result.kt"),
)

REPOSITORY_ASSETS = (
    Path("assets/repository/DomainMapper.kt"),
    Path("assets/repository/FetchData.kt"),
    Path("assets/repository/NetworkResult.kt"),
)

ANALYTICS_ASSETS = (
    Path("assets/repository/analytics/AppAnalytics.kt"),
    Path("assets/repository/analytics/AnalyticsManager.kt"),
    Path("assets/repository/analytics/events/AnalyticsEvent.kt"),
    Path("assets/repository/analytics/events/AnalyticsIdentification.kt"),
    Path("assets/repository/analytics/trackers/Analytics.kt"),
    Path("assets/repository/analytics/trackers/FirebaseAnalyticsTracker.kt"),
    Path("assets/repository/analytics/trackers/LogcatAnalyticsTracker.kt"),
    Path("assets/repository/analytics/di/AnalyticsModule.kt"),
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Copy architecture assets into their target modules and replace "
            f"{DEFAULT_PACKAGE} with the selected base package."
        )
    )
    parser.add_argument(
        "group",
        choices=("domain", "repository", "analytics", "all"),
        help="Asset group to install.",
    )
    parser.add_argument(
        "--target-root",
        type=Path,
        required=True,
        help="Root directory containing the Android modules.",
    )
    parser.add_argument(
        "--base-package",
        default=DEFAULT_PACKAGE,
        help=f"Application package. Default: {DEFAULT_PACKAGE}",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print planned files without writing them.",
    )
    parser.add_argument(
        "--force",
        action="store_true",
        help="Overwrite files that already exist.",
    )
    return parser.parse_args()


def validate_package(value: str) -> str:
    if not PACKAGE_PATTERN.fullmatch(value):
        raise ValueError(
            "base package must contain lowercase dot-separated identifiers"
        )
    return value


def selected_assets(group: str) -> tuple[Path, ...]:
    if group == "domain":
        return DOMAIN_ASSETS
    if group == "repository":
        return DOMAIN_ASSETS + REPOSITORY_ASSETS
    if group == "analytics":
        return ANALYTICS_ASSETS
    return DOMAIN_ASSETS + REPOSITORY_ASSETS + ANALYTICS_ASSETS


def destination_for(
    relative_source: Path,
    target_root: Path,
    package_path: Path,
) -> Path:
    if relative_source == Path("assets/usecase/domain/Result.kt"):
        return (
            target_root
            / "domain/src/main/java"
            / package_path
            / "domain/Result.kt"
        )

    analytics_root = Path("assets/repository/analytics")
    try:
        analytics_relative = relative_source.relative_to(analytics_root)
    except ValueError:
        analytics_relative = None

    if analytics_relative is not None:
        return (
            target_root
            / "analytics/src/main/java"
            / package_path
            / "analytics"
            / analytics_relative
        )

    return (
        target_root
        / "networking/src/main/java"
        / package_path
        / "repository"
        / relative_source.name
    )


def build_plan(
    group: str,
    target_root: Path,
    base_package: str,
) -> dict[Path, str]:
    package_path = Path(*base_package.split("."))
    plan: dict[Path, str] = {}

    for relative_source in selected_assets(group):
        source = SKILL_ROOT / relative_source
        if not source.is_file():
            raise FileNotFoundError(f"missing skill asset: {source}")

        destination = destination_for(
            relative_source=relative_source,
            target_root=target_root,
            package_path=package_path,
        )
        content = source.read_text(encoding="utf-8").replace(
            DEFAULT_PACKAGE,
            base_package,
        )
        plan[destination] = content

    return plan


def apply_plan(
    plan: dict[Path, str],
    *,
    dry_run: bool,
    force: bool,
) -> None:
    conflicts = [path for path in plan if path.exists() and not force]
    if conflicts:
        formatted = "\n".join(f"  - {path}" for path in conflicts)
        raise FileExistsError(
            "refusing to overwrite existing files; use --force:\n" + formatted
        )

    for path, content in plan.items():
        action = "OVERWRITE" if path.exists() else "CREATE"
        print(f"{action} {path}")
        if dry_run:
            continue
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")


def main() -> int:
    args = parse_args()
    try:
        base_package = validate_package(args.base_package)
        target_root = args.target_root.expanduser().resolve()
        plan = build_plan(
            group=args.group,
            target_root=target_root,
            base_package=base_package,
        )
        apply_plan(plan, dry_run=args.dry_run, force=args.force)
    except (FileExistsError, FileNotFoundError, ValueError) as error:
        print(f"error: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
