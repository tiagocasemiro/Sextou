#!/usr/bin/env python3
"""Generate repetitive Kotlin scaffolds for the registered architecture."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path
from textwrap import dedent


DEFAULT_PACKAGE = "com.example.app"
PACKAGE_PATTERN = re.compile(r"^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$")
PACKAGE_SUFFIX_PATTERN = re.compile(
    r"^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)*$"
)
PASCAL_PATTERN = re.compile(r"^[A-Z][A-Za-z0-9]*$")
LOWER_CAMEL_PATTERN = re.compile(r"^[a-z][A-Za-z0-9]*$")
PARAMETER_PATTERN = re.compile(
    r"^(?P<name>[a-z][A-Za-z0-9_]*):\s*(?P<type>[^;{}\n]+)$"
)


def common_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(add_help=False)
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
        "--feature",
        required=True,
        help="Lowercase feature package, for example user or checkout.payment.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print planned files without writing them.",
    )
    parser.add_argument(
        "--force",
        action="store_true",
        help="Overwrite generated files that already exist.",
    )
    return parser


def add_parameters(parser: argparse.ArgumentParser) -> None:
    parser.add_argument(
        "--parameter",
        action="append",
        default=[],
        help="Repeatable Kotlin parameter in the form 'name: Type'.",
    )
    parser.add_argument(
        "--import",
        dest="imports",
        action="append",
        default=[],
        help="Repeatable additional Kotlin import.",
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate layer scaffolds without overwriting by default."
    )
    subparsers = parser.add_subparsers(dest="layer", required=True)
    common = common_parser()

    repository = subparsers.add_parser(
        "repository",
        parents=[common],
        help="Generate a Repository contract and its remote or local implementation.",
    )
    repository.add_argument(
        "--name",
        required=True,
        help="PascalCase base name without Repository, for example User.",
    )
    repository.add_argument(
        "--operation",
        default="execute",
        help="lowerCamelCase remote operation. Default: execute",
    )
    repository.add_argument(
        "--result-type",
        default="Unit",
        help="Kotlin domain result type. Default: Unit",
    )
    repository.add_argument(
        "--source",
        choices=("remote", "local"),
        default="remote",
        help="Data source and implementation module. Default: remote",
    )
    repository.add_argument(
        "--stream",
        action="store_true",
        help="Generate a Flow-returning local query. Valid only with --source local.",
    )
    add_parameters(repository)

    usecase = subparsers.add_parser(
        "usecase",
        parents=[common],
        help="Generate a UseCase backed by a Repository.Remote contract.",
    )
    usecase.add_argument(
        "--name",
        required=True,
        help="PascalCase base name without UseCase, for example GetUser.",
    )
    usecase.add_argument(
        "--repository",
        required=True,
        help="PascalCase Repository base name without suffix.",
    )
    usecase.add_argument(
        "--operation",
        default="execute",
        help="Repository operation invoked by the UseCase. Default: execute",
    )
    usecase.add_argument(
        "--result-type",
        default="Unit",
        help="Kotlin domain result type. Default: Unit",
    )
    add_parameters(usecase)

    viewmodel = subparsers.add_parser(
        "viewmodel",
        parents=[common],
        help="Generate UiState, UiEvent, and ViewModel files.",
    )
    viewmodel.add_argument(
        "--screen",
        required=True,
        help="PascalCase screen name, for example UserDetails.",
    )
    viewmodel.add_argument(
        "--screen-package",
        help="Lowercase screen package. Defaults to the lowercase screen name.",
    )
    viewmodel.add_argument(
        "--usecase",
        required=True,
        help="PascalCase UseCase base name without suffix.",
    )
    add_parameters(viewmodel)

    view = subparsers.add_parser(
        "view",
        parents=[common],
        help="Generate Destination, Screen, component, route, and graph files.",
    )
    view.add_argument(
        "--screen",
        required=True,
        help="PascalCase screen name, for example UserDetails.",
    )
    view.add_argument(
        "--screen-package",
        help="Lowercase screen package. Defaults to the lowercase screen name.",
    )

    return parser.parse_args()


def validate_pascal(value: str, label: str) -> str:
    if not PASCAL_PATTERN.fullmatch(value):
        raise ValueError(f"{label} must be a PascalCase Kotlin identifier")
    return value


def validate_lower_camel(value: str, label: str) -> str:
    if not LOWER_CAMEL_PATTERN.fullmatch(value):
        raise ValueError(f"{label} must be a lowerCamelCase Kotlin identifier")
    return value


def validate_package(value: str) -> str:
    if not PACKAGE_PATTERN.fullmatch(value):
        raise ValueError(
            "base package must contain lowercase dot-separated identifiers"
        )
    return value


def validate_package_suffix(value: str, label: str) -> str:
    if not PACKAGE_SUFFIX_PATTERN.fullmatch(value):
        raise ValueError(
            f"{label} must contain lowercase dot-separated identifiers"
        )
    return value


def validate_type(value: str) -> str:
    if not value.strip() or any(token in value for token in (";", "{", "}", "\n")):
        raise ValueError("result type must be a single safe Kotlin type")
    return value.strip()


def validate_imports(values: list[str]) -> list[str]:
    imports: list[str] = []
    for value in values:
        if not re.fullmatch(r"[A-Za-z_][A-Za-z0-9_.]*", value):
            raise ValueError(f"invalid Kotlin import: {value}")
        if value not in imports:
            imports.append(value)
    return imports


def parse_parameters(values: list[str]) -> list[tuple[str, str]]:
    parameters: list[tuple[str, str]] = []
    names: set[str] = set()
    for value in values:
        match = PARAMETER_PATTERN.fullmatch(value.strip())
        if not match:
            raise ValueError(
                f"invalid parameter '{value}'; expected lowerCamelName: Type"
            )
        name = match.group("name")
        if name in names:
            raise ValueError(f"duplicate parameter: {name}")
        names.add(name)
        parameters.append((name, match.group("type").strip()))
    return parameters


def package_path(package_name: str) -> Path:
    return Path(*package_name.split("."))


def source_path(
    target_root: Path,
    module: str,
    package_name: str,
    file_name: str,
) -> Path:
    return (
        target_root
        / module
        / "src/main/java"
        / package_path(package_name)
        / file_name
    )


def render_imports(values: list[str]) -> str:
    unique = list(dict.fromkeys(values))
    return "\n".join(f"import {value}" for value in unique)


def render_parameters(parameters: list[tuple[str, str]]) -> str:
    return ", ".join(f"{name}: {type_name}" for name, type_name in parameters)


def render_arguments(parameters: list[tuple[str, str]]) -> str:
    return ", ".join(name for name, _ in parameters)


def repository_plan(args: argparse.Namespace, root: Path) -> dict[Path, str]:
    name = validate_pascal(args.name, "repository name")
    operation = validate_lower_camel(args.operation, "operation")
    result_type = validate_type(args.result_type)
    parameters = parse_parameters(args.parameter)
    extra_imports = validate_imports(args.imports)
    feature = validate_package_suffix(args.feature, "feature")
    base = validate_package(args.base_package)
    domain_package = f"{base}.domain.{feature}.repository"
    parameter_code = render_parameters(parameters)
    signature = f"{operation}({parameter_code})"

    if args.stream and args.source != "local":
        raise ValueError("--stream is valid only with --source local")

    if args.source == "local":
        adapter_package = f"{base}.local.adapter"
        contract_imports = [
            *(["kotlinx.coroutines.flow.Flow"] if args.stream else []),
            *extra_imports,
        ]
        contract_return = (
            f"Flow<{result_type}>" if args.stream else result_type
        )
        contract_modifier = "" if args.stream else "suspend "
        contract = (
            f"package {domain_package}\n\n"
            + render_imports(contract_imports)
            + ("\n\n" if contract_imports else "\n")
            + dedent(
                f"""\
                interface {name}Repository {{
                    interface Local {{
                        {contract_modifier}fun {signature}: {contract_return}
                    }}
                }}
                """
            )
        )
        local = (
            f"package {adapter_package}\n\n"
            + render_imports(
                [
                    f"{domain_package}.{name}Repository",
                    *(["kotlinx.coroutines.flow.Flow"] if args.stream else []),
                    *extra_imports,
                ]
            )
            + "\n\n"
            + dedent(
                f"""\
                class {name}LocalImpl : {name}Repository.Local {{
                    override {contract_modifier}fun {signature}: {contract_return} {{
                        TODO("Inject the Room DAO and map database types to domain types")
                    }}
                }}
                """
            )
        )
        return {
            source_path(
                root,
                "domain",
                domain_package,
                f"{name}Repository.kt",
            ): contract,
            source_path(
                root,
                "local",
                adapter_package,
                f"{name}LocalImpl.kt",
            ): local,
        }

    adapter_package = f"{base}.networking.adapter"
    contract = (
        f"package {domain_package}\n\n"
        + render_imports([f"{base}.domain.Result", *extra_imports])
        + "\n\n"
        + dedent(
            f"""\
            interface {name}Repository {{
                interface Remote {{
                    suspend fun {signature}: Result<{result_type}>
                }}
            }}
            """
        )
    )

    remote = (
        f"package {adapter_package}\n\n"
        + render_imports(
            [
                f"{base}.domain.Result",
                f"{domain_package}.{name}Repository",
                f"{base}.repository.fetchData",
                *extra_imports,
            ]
        )
        + "\n\n"
        + dedent(
            f"""\
            class {name}RemoteImpl : {name}Repository.Remote {{
                override suspend fun {signature}: Result<{result_type}> {{
                    return fetchData {{
                        TODO("Call the gateway and extract the network response")
                    }}
                }}
            }}
            """
        )
    )

    return {
        source_path(
            root,
            "domain",
            domain_package,
            f"{name}Repository.kt",
        ): contract,
        source_path(
            root,
            "networking",
            adapter_package,
            f"{name}RemoteImpl.kt",
        ): remote,
    }


def usecase_plan(args: argparse.Namespace, root: Path) -> dict[Path, str]:
    name = validate_pascal(args.name, "UseCase name")
    repository = validate_pascal(args.repository, "repository name")
    operation = validate_lower_camel(args.operation, "operation")
    result_type = validate_type(args.result_type)
    parameters = parse_parameters(args.parameter)
    extra_imports = validate_imports(args.imports)
    feature = validate_package_suffix(args.feature, "feature")
    base = validate_package(args.base_package)
    usecase_package = f"{base}.domain.{feature}.usecase"
    repository_package = f"{base}.domain.{feature}.repository"
    parameter_code = render_parameters(parameters)
    argument_code = render_arguments(parameters)

    content = (
        f"package {usecase_package}\n\n"
        + render_imports(
            [
                f"{base}.domain.Result",
                f"{repository_package}.{repository}Repository",
                *extra_imports,
            ]
        )
        + "\n\n"
        + dedent(
            f"""\
            class {name}UseCase(
                private val repository: {repository}Repository.Remote
            ) {{
                suspend operator fun invoke({parameter_code}): Result<{result_type}> {{
                    return repository.{operation}({argument_code})
                }}
            }}
            """
        )
    )

    return {
        source_path(
            root,
            "domain",
            usecase_package,
            f"{name}UseCase.kt",
        ): content
    }


def viewmodel_plan(args: argparse.Namespace, root: Path) -> dict[Path, str]:
    screen = validate_pascal(args.screen, "screen")
    usecase = validate_pascal(args.usecase, "UseCase name")
    feature = validate_package_suffix(args.feature, "feature")
    screen_package = validate_package_suffix(
        args.screen_package or screen.lower(),
        "screen package",
    )
    parameters = parse_parameters(args.parameter)
    extra_imports = validate_imports(args.imports)
    base = validate_package(args.base_package)
    ui_package = f"{base}.features.{feature}.{screen_package}"
    usecase_package = f"{base}.domain.{feature}.usecase"
    parameter_code = render_parameters(parameters)
    argument_code = render_arguments(parameters)

    ui_state = dedent(
        f"""\
        package {ui_package}

        data class {screen}UiState(
            val isLoading: Boolean = false,
            val errorMessage: String? = null
        )
        """
    )

    ui_event = dedent(
        f"""\
        package {ui_package}

        sealed interface {screen}UiEvent {{
            data object NavigateBack : {screen}UiEvent
        }}
        """
    )

    viewmodel = (
        f"package {ui_package}\n\n"
        + render_imports(
            [
                "androidx.lifecycle.ViewModel",
                "androidx.lifecycle.viewModelScope",
                f"{base}.domain.Failure",
                f"{base}.domain.Loading",
                f"{base}.domain.Success",
                f"{usecase_package}.{usecase}UseCase",
                "kotlinx.coroutines.flow.MutableSharedFlow",
                "kotlinx.coroutines.flow.MutableStateFlow",
                "kotlinx.coroutines.flow.SharedFlow",
                "kotlinx.coroutines.flow.StateFlow",
                "kotlinx.coroutines.flow.asSharedFlow",
                "kotlinx.coroutines.flow.asStateFlow",
                "kotlinx.coroutines.flow.update",
                "kotlinx.coroutines.launch",
                *extra_imports,
            ]
        )
        + "\n\n"
        + dedent(
            f"""\
            class {screen}ViewModel(
                private val useCase: {usecase}UseCase
            ) : ViewModel() {{
                private val mutableUiState = MutableStateFlow({screen}UiState())
                val uiState: StateFlow<{screen}UiState> = mutableUiState.asStateFlow()

                private val mutableEvents = MutableSharedFlow<{screen}UiEvent>(
                    extraBufferCapacity = 1
                )
                val events: SharedFlow<{screen}UiEvent> = mutableEvents.asSharedFlow()

                fun load({parameter_code}) {{
                    viewModelScope.launch {{
                        mutableUiState.update {{
                            it.copy(isLoading = true, errorMessage = null)
                        }}

                        when (val result = useCase({argument_code})) {{
                            is Success -> mutableUiState.update {{
                                it.copy(isLoading = false)
                            }}
                            is Failure -> mutableUiState.update {{
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.error
                                        ?.formattedMessage
                                        .orEmpty()
                                )
                            }}
                            is Loading -> Unit
                        }}
                    }}
                }}

                fun onBackClicked() {{
                    mutableEvents.tryEmit({screen}UiEvent.NavigateBack)
                }}
            }}
            """
        )
    )

    return {
        source_path(
            root,
            "features",
            ui_package,
            f"{screen}UiState.kt",
        ): ui_state,
        source_path(
            root,
            "features",
            ui_package,
            f"{screen}UiEvent.kt",
        ): ui_event,
        source_path(
            root,
            "features",
            ui_package,
            f"{screen}ViewModel.kt",
        ): viewmodel,
    }


def view_plan(args: argparse.Namespace, root: Path) -> dict[Path, str]:
    screen = validate_pascal(args.screen, "screen")
    feature = validate_package_suffix(args.feature, "feature")
    screen_package = validate_package_suffix(
        args.screen_package or screen.lower(),
        "screen package",
    )
    base = validate_package(args.base_package)
    ui_package = f"{base}.features.{feature}.{screen_package}"
    components_package = f"{ui_package}.components"
    navigation_package = f"{base}.navigation"
    destination_function = screen[0].lower() + screen[1:] + "Destination"

    destination = dedent(
        f"""\
        package {ui_package}

        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.LaunchedEffect
        import androidx.compose.runtime.getValue
        import androidx.lifecycle.Lifecycle
        import androidx.lifecycle.compose.collectAsStateWithLifecycle
        import androidx.lifecycle.repeatOnLifecycle
        import androidx.lifecycle.compose.LocalLifecycleOwner
        import org.koin.compose.viewmodel.koinNavViewModel

        @Composable
        fun {screen}Destination(
            onNavigateBack: () -> Unit,
            viewModel: {screen}ViewModel = koinNavViewModel()
        ) {{
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val lifecycleOwner = LocalLifecycleOwner.current

            LaunchedEffect(viewModel, lifecycleOwner) {{
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {{
                    viewModel.events.collect {{ event ->
                        when (event) {{
                            {screen}UiEvent.NavigateBack -> onNavigateBack()
                        }}
                    }}
                }}
            }}

            {screen}Screen(
                uiState = uiState,
                onRetry = viewModel::load,
                onBackClick = viewModel::onBackClicked
            )
        }}
        """
    )

    screen_content = dedent(
        f"""\
        package {ui_package}

        import androidx.compose.foundation.layout.Box
        import androidx.compose.foundation.layout.fillMaxSize
        import androidx.compose.material3.Button
        import androidx.compose.material3.CircularProgressIndicator
        import androidx.compose.material3.Text
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import {components_package}.{screen}Content

        @Composable
        fun {screen}Screen(
            uiState: {screen}UiState,
            onRetry: () -> Unit,
            onBackClick: () -> Unit,
            modifier: Modifier = Modifier
        ) {{
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {{
                when {{
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.errorMessage != null -> Button(onClick = onRetry) {{
                        Text(text = uiState.errorMessage)
                    }}
                    else -> {screen}Content(onBackClick = onBackClick)
                }}
            }}
        }}
        """
    )

    component = dedent(
        f"""\
        package {components_package}

        import androidx.compose.material3.Button
        import androidx.compose.material3.Text
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Modifier

        @Composable
        internal fun {screen}Content(
            onBackClick: () -> Unit,
            modifier: Modifier = Modifier
        ) {{
            Button(
                onClick = onBackClick,
                modifier = modifier
            ) {{
                Text(text = "{screen}")
            }}
        }}
        """
    )

    route = dedent(
        f"""\
        package {navigation_package}

        import kotlinx.serialization.Serializable

        @Serializable
        data object {screen}Route
        """
    )

    navigation = dedent(
        f"""\
        package {ui_package}

        import androidx.navigation.NavGraphBuilder
        import androidx.navigation.NavHostController
        import androidx.navigation.compose.composable
        import {navigation_package}.{screen}Route

        fun NavGraphBuilder.{destination_function}(
            navController: NavHostController
        ) {{
            composable<{screen}Route> {{
                {screen}Destination(
                    onNavigateBack = navController::popBackStack
                )
            }}
        }}
        """
    )

    return {
        source_path(
            root,
            "features",
            ui_package,
            f"{screen}Destination.kt",
        ): destination,
        source_path(
            root,
            "features",
            ui_package,
            f"{screen}Screen.kt",
        ): screen_content,
        source_path(
            root,
            "features",
            components_package,
            f"{screen}Content.kt",
        ): component,
        source_path(
            root,
            "features",
            ui_package,
            f"{screen}Navigation.kt",
        ): navigation,
        source_path(
            root,
            "features",
            navigation_package,
            f"{screen}Route.kt",
        ): route,
    }


def build_plan(args: argparse.Namespace, root: Path) -> dict[Path, str]:
    builders = {
        "repository": repository_plan,
        "usecase": usecase_plan,
        "viewmodel": viewmodel_plan,
        "view": view_plan,
    }
    return builders[args.layer](args, root)


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
        target_root = args.target_root.expanduser().resolve()
        plan = build_plan(args, target_root)
        apply_plan(plan, dry_run=args.dry_run, force=args.force)
    except (FileExistsError, ValueError) as error:
        print(f"error: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
