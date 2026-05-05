# Portainer Ops MCP Java Server Public Sample

## 1. 概要

本リポジトリは、Portainer / Docker 環境を LLM から観測するために作成した、Java 製 MCP Server の公開用サンプルです。

個人開発環境で運用している Java MCP Server から、公開可能な範囲を抽出し、単独でビルド・確認できる形に整理したものです。

主な目的は、以下の技術要素を示すことです。

- Java Servlet による MCP / JSON-RPC 風のサーバー実装
- `initialize` / `tools/list` / `tools/call` の基本的な処理構造
- Portainer / Docker API 由来の情報を LLM が扱いやすい DTO に変換する設計
- Docker 環境を read-only で観測するための Tool 設計
- 個人開発コードから公開可能な最小単位を切り出す際のマスク・公開方針

このリポジトリは、完成品の汎用ライブラリではなく、AI / LLM システム開発ポートフォリオの一部として公開している実装サンプルです。

## 2. このリポジトリで示したいこと

本実装の主題は、単に MCP Server を起動することではありません。

重要なのは、低レイヤーな Portainer / Docker API の情報をそのまま LLM に渡すのではなく、LLM が判断しやすい粒度へ再構成している点です。

例えば Docker API のレスポンスは、人間のエンジニアが読むには十分でも、LLM が安定して状況判断するには情報量や構造が粗すぎる場合があります。

そのため本実装では、以下のような段階に分けて情報を整理しています。

1. Portainer / Docker API から情報を取得する
2. API レスポンスを Java クラスとしてパースする
3. 監視・説明・障害調査に必要な情報へ変換する
4. LLM が扱いやすい JSON DTO として返却する

この構成により、LLM に対して「低レイヤー API を直接操作させる」のではなく、「観測用途に整理された Tool を使わせる」設計にしています。

## 3. 公開範囲について

本リポジトリは、個人開発環境で運用中のコードをそのまま公開したものではありません。

公開にあたり、以下の情報は含めていません。

- 実運用環境の API キー
- Portainer の実トークン
- 実IPアドレス
- 実ホスト名
- 実コンテナ名
- 実環境を特定できる情報
- 個人開発環境用の Docker Compose
- 個人開発環境の運用ログ
- 自作共通ライブラリ全体

一方で、MCP Server としての構造、Portainer / Docker 情報の扱い方、LLM 向け DTO の設計意図が分かる範囲は残しています。

## 4. 注意事項

このリポジトリは、公開用に抽出したサンプルです。

本番環境でそのまま利用することは想定していません。

特に、以下の点には注意してください。

- CORS 設定はローカル検証向けです
- 認証・認可は簡略化されています
- Portainer / Docker に対する破壊的操作は実装していません
- read-only の観測用途に限定しています
- 実運用する場合は、接続元 Origin、認証、ログ出力、例外メッセージ、トークン管理を環境に合わせて見直してください

## 5. 提供する MCP Tools

本サンプルでは、以下の3つの Tool を提供します。

### 5.1 environments

Portainer に登録されている Docker 環境の一覧・概要を返します。

想定用途:

- 利用可能な Docker ホストの確認
- ホスト単位の概要把握
- 後続の `containers` 呼び出しに使う `environmentName` の確認

LLM には、最初に呼ぶ入口 Tool として使わせる想定です。

### 5.2 containers

指定した Docker 環境に属するコンテナ一覧を返します。

想定用途:

- どのコンテナが稼働しているか確認する
- コンテナの状態、イメージ、ポート、マウント、ネットワークモードを確認する
- CPU、メモリ、PID などの簡易的なリソース状況を確認する
- `incidentDetail` で深掘りすべき対象コンテナを特定する

### 5.3 incidentDetail

指定したコンテナの障害調査向け詳細情報を返します。

想定用途:

- 再起動回数の確認
- OOM Kill の確認
- exitCode の確認
- 開始時刻・終了時刻の確認
- CPU / メモリ / PID の確認
- 直近ログの確認

この Tool は、通常監視ではなく、特定コンテナの調査が必要な場合に使う想定です。

## 6. ディレクトリ構成

```text
src/main/java/vr46/portaineropsmcppublic/
  config/
    AppConfig.java

  mcp/
    BaseMcpServlet.java
    InitializeRequest.java
    InitializeResponse.java
    ToolsListRequest.java
    ToolsListResponse.java
    ToolsCallRequest.java
    ToolsCallResponse.java

  web/
    McpServlet.java

  portainer/
    PortainerMcpApiCore.java
    PortainerApiCallException.java
    ListEnvironmentsResponse.java
    DockerProxy_infoResponse.java
    DockerProxy_containersResponse.java
    DockerProxy_containerResponse.java
    DockerProxy_containerStatsResponse.java

  dto/
    Environments.java
    EnvironmentOverview.java
    Containers.java
    ContainerOverview.java
    IncidentDetail.java

  converter/
    EnvironmentsOverviewConverter.java
    ContainersOverviewConverter.java
    IncidentDetailConverter.java

  logging/
    MyLogger.java
```

## 7. 主なクラスの役割

### 7.1 BaseMcpServlet

MCP Server としての共通処理を担当します。

主な責務:

- HTTP POST の受付
- JSON-RPC 形式の検証
- `initialize` の処理
- `tools/list` の処理
- `tools/call` の処理
- MCP session ID の発行・検証
- 基本的なエラーレスポンス生成
- CORS ヘッダー付与

このクラスは、個別の Tool 実装に依存しない MCP 共通基盤として作成しています。

### 7.2 McpServlet

Portainer / Docker 観測用 Tool を定義する Servlet です。

主な責務:

- `environments` Tool の定義
- `containers` Tool の定義
- `incidentDetail` Tool の定義
- LLM 向け instructions の設定
- 各 Tool 呼び出しを Converter 層へ委譲

本実装では、破壊的操作を提供せず、read-only の観測用途に限定しています。

### 7.3 PortainerMcpApiCore

Portainer MCP Proxy / Portainer API 側への HTTP 呼び出しを担当します。

主な責務:

- API エンドポイントへの POST
- Bearer Token の付与
- レスポンスコードの取得
- レスポンス Body の取得
- Docker Proxy API 呼び出し
- API 呼び出し失敗時の例外化

公開版では、接続先 URL や Token はソースコードに直書きせず、環境変数経由で取得します。

### 7.4 DTO classes

`dto` パッケージ配下のクラスは、LLM へ返すための正規化済み JSON 構造を表します。

代表例:

- `EnvironmentOverview`
- `ContainerOverview`
- `IncidentDetail`

これらは、Docker API のレスポンスをそのまま返すのではなく、LLM が状態把握・比較・説明しやすい形へ整理したものです。

### 7.5 Converter classes

`converter` パッケージ配下のクラスは、Portainer / Docker API のパース結果を、LLM 向け DTO へ変換します。

代表例:

- `EnvironmentsOverviewConverter`
- `ContainersOverviewConverter`
- `IncidentDetailConverter`

この層により、API レスポンスの形式と、LLM に返す形式を分離しています。

## 8. 設定

本プロジェクトでは、接続先 URL や API Token をソースコードに直書きしません。

必要な値は環境変数として渡します。

公開用の設定例は `.env.example` に記載しています。

```env
PORTAINER_BASE_URL=http://localhost:8010/
PORTAINER_API_TOKEN=replace-with-your-token
PORTAINER_ENVIRONMENT_ID=1
MCP_PROTOCOL_VERSION=2025-06-18
```

ローカルで `.env` を使う場合は、以下のようにコピーして使用します。

```bash
cp .env.example .env
```

ただし、Java は標準では `.env` を自動読み込みしません。

IntelliJ IDEA、Tomcat、Docker Compose、シェルなど、実行環境側で環境変数として渡してください。

`.env` には秘密情報やローカル環境固有の値が入るため、Git 管理対象外にしています。

## 9. ビルド

Maven Wrapper を使用します。

Windows PowerShell:

```powershell
.\mvnw.cmd clean package
```

Linux / macOS:

```bash
./mvnw clean package
```

ビルド成果物は `target/` 配下に生成されます。

## 10. 実行方法

このプロジェクトは Java Servlet ベースの Web アプリケーションです。

Tomcat などの Servlet Container にデプロイして使用します。

例:

```text
http://localhost:8080/portainer-ops-mcp-public/mcp
```

実際の URL は、デプロイ先のコンテキストパスにより変わります。

## 11. MCP 呼び出し例

### 11.1 initialize

Request:

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2025-06-18",
    "capabilities": {},
    "clientInfo": {
      "name": "sample-client",
      "version": "0.1.0"
    }
  }
}
```

Response では、`Mcp-Session-Id` ヘッダーが返ります。

以降の `tools/list` や `tools/call` では、この session ID を `Mcp-Session-Id` ヘッダーに指定します。

### 11.2 tools/list

Request:

```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/list",
  "params": {}
}
```

Response には、利用可能な Tool と input schema が含まれます。

### 11.3 tools/call: environments

Request:

```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "method": "tools/call",
  "params": {
    "name": "environments",
    "arguments": {}
  }
}
```

### 11.4 tools/call: containers

Request:

```json
{
  "jsonrpc": "2.0",
  "id": 4,
  "method": "tools/call",
  "params": {
    "name": "containers",
    "arguments": {
      "environmentName": "sample-docker-host"
    }
  }
}
```

### 11.5 tools/call: incidentDetail

Request:

```json
{
  "jsonrpc": "2.0",
  "id": 5,
  "method": "tools/call",
  "params": {
    "name": "incidentDetail",
    "arguments": {
      "environmentName": "sample-docker-host",
      "containerName": "sample-container"
    }
  }
}
```

## 12. 設計上の特徴

### 12.1 LLM に直接低レイヤー API を触らせない

Portainer / Docker API は強力ですが、LLM にそのまま扱わせるには低レイヤーすぎる場合があります。

本実装では、Docker API の生レスポンスをそのまま Tool の戻り値にするのではなく、以下のような観点で再構成しています。

- ホスト単位の概要
- コンテナ単位の状態
- 通常監視で見る情報
- 障害調査時だけ見る情報
- LLM が次に呼ぶべき Tool を判断しやすい名前・説明・引数

これにより、LLM が Tool を選択しやすくなり、ユーザーへの説明も安定しやすくなります。

### 12.2 read-only に限定

本実装では、Docker コンテナの起動、停止、削除、再起動、作成、更新などの操作を提供していません。

理由は、LLM Tool Use においては、最初から操作権限を与えるよりも、まず観測・説明・状況整理に限定した方が安全だからです。

### 12.3 DTO による情報整理

Docker API のレスポンスは、非常に多くの情報を含みます。

本実装では、LLM が状況判断しやすいように、以下のような DTO へ変換しています。

- host / environment overview
- container overview
- incident detail
- state detail
- stats
- risk flags

これにより、LLM は「どのフィールドを見るべきか」を判断しやすくなります。

## 13. セキュリティ方針

本リポジトリでは、以下の方針で公開しています。

- API Token はコミットしない
- `.env` はコミットしない
- `.env.example` のみ公開する
- 実IPアドレスは記載しない
- 実ホスト名は記載しない
- 実コンテナ名は記載しない
- 内部環境を推測できるログは記載しない
- Docker に対する破壊的 Tool は提供しない
- read-only の観測用途に限定する

## 14. このリポジトリの位置づけ

このリポジトリは、以下の技術ポートフォリオの一部です。

- ローカル LLM 基盤
- RAG / ETL
- QLoRA
- 自律型AIエージェント
- MCP / Tool Use
- AI伴走型開発

その中で本リポジトリは、特に MCP / Tool Use の実装証跡として位置づけています。

LLM を単なるチャット応答に閉じず、外部システムの状態を安全に観測させるための設計例です。

## 15. License

- MIT License


## 16. Author

5972e24kg-collab
