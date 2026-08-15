#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Uso: $0 <nome-da-ferramenta> '<argumentos-json>'" >&2
  exit 64
fi

tool_name=$1
tool_arguments=$2

if ! command -v curl >/dev/null 2>&1 || ! command -v jq >/dev/null 2>&1; then
  echo "Esta ferramenta requer curl e jq." >&2
  exit 69
fi

if ! jq -e 'type == "object"' >/dev/null 2>&1 <<<"$tool_arguments"; then
  echo "O segundo argumento deve ser um objeto JSON válido." >&2
  exit 65
fi

github_token=${GITHUB_PAT_TOKEN:-}
if [[ -z "$github_token" ]] && command -v systemctl >/dev/null 2>&1; then
  github_token=$(systemctl --user show-environment 2>/dev/null | sed -n 's/^GITHUB_PAT_TOKEN=//p')
fi

if [[ -z "$github_token" ]]; then
  echo "GITHUB_PAT_TOKEN não está disponível nesta sessão nem no ambiente do usuário." >&2
  exit 77
fi

mcp_url=https://api.githubcopilot.com/mcp/
mcp_protocol=2025-03-26
mcp_headers=$(mktemp "${TMPDIR:-/tmp}/sextou-github-mcp.XXXXXX")
trap 'rm -f "$mcp_headers"' EXIT

initialize_payload=$(jq -nc --arg protocol "$mcp_protocol" '{
  jsonrpc: "2.0",
  id: 1,
  method: "initialize",
  params: {
    protocolVersion: $protocol,
    capabilities: {},
    clientInfo: {name: "manage-sextou-tasks", version: "1.0"}
  }
}')

curl --fail-with-body --silent --show-error --max-time 30 \
  --dump-header "$mcp_headers" --output /dev/null \
  --header "Authorization: Bearer $github_token" \
  --header "Content-Type: application/json" \
  --header "Accept: application/json, text/event-stream" \
  --header "X-MCP-Toolsets: default,projects" \
  --data "$initialize_payload" \
  "$mcp_url"

mcp_session=$(sed -n 's/^[Mm][Cc][Pp]-[Ss]ession-[Ii][Dd]:[[:space:]]*//p' "$mcp_headers" | tr -d '\r')
if [[ -z "$mcp_session" ]]; then
  echo "O GitHub MCP não retornou Mcp-Session-Id." >&2
  exit 70
fi

common_headers=(
  --header "Authorization: Bearer $github_token"
  --header "Mcp-Session-Id: $mcp_session"
  --header "Mcp-Protocol-Version: $mcp_protocol"
  --header "Content-Type: application/json"
  --header "Accept: application/json, text/event-stream"
  --header "X-MCP-Toolsets: default,projects"
)

curl --fail-with-body --silent --show-error --max-time 30 --output /dev/null \
  "${common_headers[@]}" \
  --data '{"jsonrpc":"2.0","method":"notifications/initialized"}' \
  "$mcp_url"

call_payload=$(jq -nc \
  --arg tool "$tool_name" \
  --argjson arguments "$tool_arguments" \
  '{jsonrpc:"2.0",id:2,method:"tools/call",params:{name:$tool,arguments:$arguments}}')

response=$(curl --fail-with-body --silent --show-error --max-time 30 \
  "${common_headers[@]}" \
  --data "$call_payload" \
  "$mcp_url" | sed -n 's/^data: //p')

if [[ -z "$response" ]]; then
  echo "O GitHub MCP retornou uma resposta vazia." >&2
  exit 70
fi

if jq -e '.error or .result.isError == true' >/dev/null 2>&1 <<<"$response"; then
  jq . <<<"$response" >&2
  exit 1
fi

jq -r '.result.content[]? | select(.type == "text") | .text' <<<"$response"
