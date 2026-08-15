#!/usr/bin/env python3
"""Extract architecture-rule candidates and asset structure from this skill."""

from __future__ import annotations

import argparse
import json
import re
from dataclasses import asdict, dataclass
from pathlib import Path


SKILL_ROOT = Path(__file__).resolve().parent.parent
REFERENCES = {
    "overview": Path("references/overview.md"),
    "repository": Path("references/repository.md"),
    "usecase": Path("references/use-case.md"),
    "viewmodel": Path("references/view-model.md"),
    "view": Path("references/view.md"),
}
NORMATIVE_PATTERN = re.compile(
    r"\b("
    r"deve|devem|não|nunca|somente|sempre|obrigatóri[oa]s?|"
    r"usar|manter|expor|receber|terminar|estar|depender|registrar|"
    r"preferir|evitar|proibir|permitir|garantir|confirmar|verificar"
    r")\b",
    re.IGNORECASE,
)
MARKER_PATTERN = re.compile(
    r"^(?P<indent>\s*)("
    r"(?P<number>\d+)\.\s+|"
    r"(?P<check>-\s+\[[ xX]\])\s+|"
    r"(?P<bullet>[-*])\s+"
    r")(?P<text>.*)$"
)
HEADING_PATTERN = re.compile(r"^(#{1,6})\s+(.+?)\s*$")
PACKAGE_PATTERN = re.compile(r"^package\s+([A-Za-z_][A-Za-z0-9_.]*)", re.MULTILINE)
IMPORT_PATTERN = re.compile(r"^import\s+([A-Za-z_][A-Za-z0-9_.*]*)", re.MULTILINE)
TYPE_DECLARATION_PATTERN = re.compile(
    r"^(?:(public|internal|private|protected)\s+)?"
    r"(?:(data|sealed|enum|value|annotation)\s+)?"
    r"(class|interface|object|typealias)\s+"
    r"([A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
FUNCTION_DECLARATION_PATTERN = re.compile(
    r"^(?:(public|internal|private|protected)\s+)?"
    r"(?P<modifiers>(?:(?:inline|suspend|operator|infix|tailrec)\s+)*)"
    r"fun\s+(?:<[^>]+>\s*)?"
    r"(?:(?:[A-Za-z_][A-Za-z0-9_<>?, .]*)\.)?"
    r"([A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
PROPERTY_DECLARATION_PATTERN = re.compile(
    r"^(?:(public|internal|private|protected)\s+)?"
    r"(?P<modifiers>(?:(?:const|lateinit)\s+)*)"
    r"(val|var)\s+([A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

PERSPECTIVE_TERMS = {
    "nomenclature": (
        "nome",
        "sufixo",
        "prefixo",
        "terminar",
        "nomenclatura",
    ),
    "packages-modules": (
        "pacote",
        "package",
        "módulo",
        "source set",
        "diretório",
        "caminho",
    ),
    "dependencies": (
        "depend",
        "acessar",
        "receber",
        "injeção",
        "contrato",
        "import",
        "gateway",
        "repository",
    ),
    "patterns-structure": (
        "interface",
        "classe",
        "class",
        "sealed",
        "data class",
        "internal",
        "private",
        "herdar",
        "aninhada",
        "construtor",
    ),
    "frameworks": (
        "compose",
        "viewmodel",
        "coroutine",
        "flow",
        "koin",
        "retrofit",
        "room",
        "firebase",
        "navigation",
        "konsist",
        "junit",
        "robolectric",
    ),
    "async-state": (
        "suspend",
        "coroutine",
        "dispatcher",
        "cancel",
        "flow",
        "state",
        "estado",
        "scope",
    ),
    "di-navigation": (
        "injeção",
        "binding",
        "factory",
        "single",
        "koin",
        "rota",
        "nav",
        "destination",
    ),
    "reusable-assets": (
        "asset",
        "result",
        "domainmapper",
        "fetchdata",
        "networkresult",
        "analytics",
        "reutiliz",
    ),
    "code-quality": (
        "imut",
        "nullable",
        "falha",
        "erro",
        "exception",
        "cancel",
        "teste",
        "órf",
        "duplic",
        "visibilidade",
    ),
}


@dataclass(frozen=True)
class RuleCandidate:
    source: str
    line: int
    section: str
    kind: str
    perspectives: tuple[str, ...]
    text: str


@dataclass(frozen=True)
class AssetDeclaration:
    visibility: str
    modifier: str
    kind: str
    name: str


@dataclass(frozen=True)
class AssetInventory:
    source: str
    package: str
    declarations: tuple[AssetDeclaration, ...]
    imports: tuple[str, ...]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Read the current skill references and Kotlin assets, then emit "
            "an inventory for architecture-test planning."
        )
    )
    parser.add_argument(
        "--scope",
        action="append",
        choices=tuple(REFERENCES),
        help="Reference scope to inspect. Repeat as needed; default: all.",
    )
    parser.add_argument(
        "--format",
        choices=("markdown", "json"),
        default="markdown",
        help="Output format. Default: markdown.",
    )
    return parser.parse_args()


def normalize_text(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip()


def perspectives_for(text: str) -> tuple[str, ...]:
    lowered = text.lower()
    matches = [
        perspective
        for perspective, terms in PERSPECTIVE_TERMS.items()
        if any(term in lowered for term in terms)
    ]
    return tuple(matches or ("general",))


def should_include(kind: str, section: str, text: str) -> bool:
    lowered_section = section.lower()
    if lowered_section == "conteúdo":
        return False
    return (
        kind == "checklist"
        or "convenções obrigatórias" in lowered_section
        or "stack de referência" in lowered_section
        or NORMATIVE_PATTERN.search(text) is not None
    )


def extract_reference(relative_path: Path) -> list[RuleCandidate]:
    path = SKILL_ROOT / relative_path
    lines = path.read_text(encoding="utf-8").splitlines()
    candidates: list[RuleCandidate] = []
    section = path.stem
    in_code = False
    pending: dict[str, object] | None = None

    def flush() -> None:
        nonlocal pending
        if pending is None:
            return
        text = normalize_text(str(pending["text"]))
        kind = str(pending["kind"])
        candidate_section = str(pending["section"])
        if text and should_include(kind, candidate_section, text):
            candidates.append(
                RuleCandidate(
                    source=relative_path.as_posix(),
                    line=int(pending["line"]),
                    section=candidate_section,
                    kind=kind,
                    perspectives=perspectives_for(text),
                    text=text,
                )
            )
        pending = None

    for line_number, raw_line in enumerate(lines, start=1):
        stripped = raw_line.strip()
        if stripped.startswith("```"):
            flush()
            in_code = not in_code
            continue
        if in_code:
            continue

        heading = HEADING_PATTERN.match(raw_line)
        if heading:
            flush()
            section = heading.group(2)
            continue

        marker = MARKER_PATTERN.match(raw_line)
        if marker:
            flush()
            if marker.group("check"):
                kind = "checklist"
            elif marker.group("number"):
                kind = "numbered"
            else:
                kind = "bullet"
            pending = {
                "line": line_number,
                "section": section,
                "kind": kind,
                "text": marker.group("text"),
                "indent": len(marker.group("indent")),
            }
            continue

        if stripped.startswith("|") and stripped.endswith("|"):
            flush()
            if not re.fullmatch(r"\|?[\s:|-]+\|?", stripped):
                text = normalize_text(stripped.strip("|").replace("|", " — "))
                if should_include("table", section, text):
                    candidates.append(
                        RuleCandidate(
                            source=relative_path.as_posix(),
                            line=line_number,
                            section=section,
                            kind="table",
                            perspectives=perspectives_for(text),
                            text=text,
                        )
                    )
            continue

        if not stripped:
            flush()
            continue

        if pending is not None and (
            raw_line.startswith(" ") or pending["kind"] == "paragraph"
        ):
            pending["text"] = f"{pending['text']} {stripped}"
            continue

        if pending is not None:
            flush()

        pending = {
            "line": line_number,
            "section": section,
            "kind": "paragraph",
            "text": stripped,
            "indent": 0,
        }

    flush()
    return candidates


def extract_assets() -> list[AssetInventory]:
    inventories: list[AssetInventory] = []
    for path in sorted((SKILL_ROOT / "assets").rglob("*.kt")):
        content = path.read_text(encoding="utf-8")
        package_match = PACKAGE_PATTERN.search(content)
        type_declarations = [
            AssetDeclaration(
                visibility=match.group(1) or "public",
                modifier=match.group(2) or "",
                kind=match.group(3),
                name=match.group(4),
            )
            for match in TYPE_DECLARATION_PATTERN.finditer(content)
        ]
        function_declarations = [
            AssetDeclaration(
                visibility=match.group(1) or "public",
                modifier=normalize_text(match.group("modifiers")),
                kind="fun",
                name=match.group(3),
            )
            for match in FUNCTION_DECLARATION_PATTERN.finditer(content)
        ]
        property_declarations = [
            AssetDeclaration(
                visibility=match.group(1) or "public",
                modifier=normalize_text(match.group("modifiers")),
                kind=match.group(3),
                name=match.group(4),
            )
            for match in PROPERTY_DECLARATION_PATTERN.finditer(content)
        ]
        declarations = tuple(
            type_declarations + function_declarations + property_declarations
        )
        inventories.append(
            AssetInventory(
                source=path.relative_to(SKILL_ROOT).as_posix(),
                package=package_match.group(1) if package_match else "",
                declarations=declarations,
                imports=tuple(IMPORT_PATTERN.findall(content)),
            )
        )
    return inventories


def render_markdown(
    candidates: list[RuleCandidate],
    assets: list[AssetInventory],
) -> str:
    output = [
        "# Architecture rule inventory",
        "",
        "Review every candidate against its complete source section.",
        "",
        "## Rule candidates",
        "",
    ]
    current_source = ""
    for candidate in candidates:
        if candidate.source != current_source:
            current_source = candidate.source
            output.extend((f"### {current_source}", ""))
        perspectives = ", ".join(candidate.perspectives)
        output.append(
            f"- `{candidate.source}:{candidate.line}` "
            f"— **{candidate.section}** — `{candidate.kind}` "
            f"— [{perspectives}] — {candidate.text}"
        )

    output.extend(("", "## Kotlin asset inventory", ""))
    for asset in assets:
        declarations = ", ".join(
            " ".join(
                part
                for part in (
                    declaration.visibility,
                    declaration.modifier,
                    declaration.kind,
                    declaration.name,
                )
                if part
            )
            for declaration in asset.declarations
        )
        output.extend(
            (
                f"### {asset.source}",
                "",
                f"- Package: `{asset.package or '(none)'}`",
                f"- Declarations: {declarations or '(none detected)'}",
                "- Imports: "
                + (
                    ", ".join(f"`{value}`" for value in asset.imports)
                    if asset.imports
                    else "(none)"
                ),
                "",
            )
        )
    return "\n".join(output).rstrip() + "\n"


def render_json(
    candidates: list[RuleCandidate],
    assets: list[AssetInventory],
) -> str:
    payload = {
        "rules": [asdict(candidate) for candidate in candidates],
        "assets": [asdict(asset) for asset in assets],
    }
    return json.dumps(payload, ensure_ascii=False, indent=2) + "\n"


def main() -> int:
    args = parse_args()
    selected_scopes = list(dict.fromkeys(args.scope or REFERENCES.keys()))
    candidates = [
        candidate
        for scope in selected_scopes
        for candidate in extract_reference(REFERENCES[scope])
    ]
    assets = extract_assets()
    if args.format == "json":
        print(render_json(candidates, assets), end="")
    else:
        print(render_markdown(candidates, assets), end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
