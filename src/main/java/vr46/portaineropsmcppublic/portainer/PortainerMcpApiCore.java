package vr46.portaineropsmcppublic.portainer;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import vr46.portaineropsmcppublic.config.AppConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PortainerMcpApiCore {
    // -----------------------------------------------------------------------
    // 接続設定
    // -----------------------------------------------------------------------
    private final String API_SERVER = AppConfig.load().portainerBaseUrl;
    private final String TOKEN = AppConfig.load().portainerApiToken;
    private static final int CONNECT_TIMEOUT_MS = 1000 * 60 * 1;    //1分
    private static final int READ_TIMEOUT_MS    = 1000 * 60 * 3;    //3分

    // -----------------------------------------------------------------------
    // 共通 API 呼び出し
    // -----------------------------------------------------------------------
    private ResponseParam apiCall(String apiPath, Object parameters) {
        String uri = API_SERVER + apiPath;
        HttpURLConnection con = null;
        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setDoOutput(true);
            con.setConnectTimeout(CONNECT_TIMEOUT_MS);
            con.setReadTimeout(READ_TIMEOUT_MS);

            // リクエストボディの書き込み
            if (parameters != null) {
                ObjectMapper mapper = new ObjectMapper();
                String parameterString = mapper.writeValueAsString(parameters);
                try (OutputStreamWriter out = new OutputStreamWriter(
                        con.getOutputStream(), StandardCharsets.UTF_8)) {
                    out.write(parameterString);
                }
            }

            // レスポンスの読み取り
            ResponseParam responseParam = new ResponseParam();
            responseParam.responseCode = con.getResponseCode();

            // 成功判定: 2xx 系すべてを成功とみなす
            responseParam.isSucceed =
                    (responseParam.responseCode >= 200 && responseParam.responseCode < 300);

            InputStream inputStream = responseParam.isSucceed
                    ? con.getInputStream()
                    : con.getErrorStream();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                responseParam.responseBody = response.toString();
            }
            return responseParam;

        } catch (Exception e) {
            ResponseParam responseParam = new ResponseParam();
            responseParam.isSucceed = false;
            responseParam.responseCode = -1; // 通信自体が失敗した場合の固定値
            // デバッグのため例外メッセージをボディに格納する
            responseParam.responseBody = "Connection Failed: " + e.getMessage();
            return responseParam;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    // -----------------------------------------------------------------------
    // レスポンス型
    // -----------------------------------------------------------------------
    public static class ResponseParam {
        public int responseCode = -999;
        public boolean isSucceed = false;
        public String responseBody = "";
    }

    // -----------------------------------------------------------------------
    // API メソッド
    // -----------------------------------------------------------------------
    /*
    //確認用に使って下さい。
    #!/usr/bin/env bash
    TOKEN='[token masked]'
    BASE='http://example-mcp-proxy.local:8010'

    curl -X 'POST' \
      '$BASE/listEnvironments' \
      -H 'accept: application/json' \
      -H 'Authorization: Bearer $TOKEN' \
      -d ''
     */
    public ListEnvironmentsResponse listEnvironments() throws Exception {
        ResponseParam responseParam = apiCall("listEnvironments", null);
        if(responseParam.isSucceed) {
            //成功時
            //System.out.println(responseParam.responseBody);
            return new ListEnvironmentsResponse(responseParam.responseBody);
        } else {
            //失敗時
            System.err.println(responseParam.responseBody);
            throw new PortainerApiCallException("listEnvironments API call failed.", responseParam);
        }
    }


    // -----------------------------------------------------------------------
    // リクエストパラメータ型
    // -----------------------------------------------------------------------
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class DockerProxyParams {
        @JsonProperty("environmentId")
        public int environmentId = 0;
        @JsonProperty("method")
        public String method = "GET";
        @JsonProperty("dockerAPIPath")
        public String dockerAPIPath = "";
        @JsonProperty("queryParams")
        public List<QueryParam> queryParams = null;
    }
    private static class QueryParam {
        @JsonProperty("key")
        public String key;
        @JsonProperty("value")
        public String value;
        public QueryParam(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /*
    //確認用に使って下さい。
    #!/usr/bin/env bash
    TOKEN='[token masked]'
    BASE='http://example-mcp-proxy.local:8010'
    ENV_ID=9

        curl -s -X POST "$BASE/dockerProxy" \
        -H 'accept: application/json' \
        -H 'Content-Type: application/json' \
        -H "Authorization: Bearer $TOKEN" \
        -d "{
          \"environmentId\": $ENV_ID,
          \"method\": \"GET\",
          \"dockerAPIPath\": \"/info\"
        }"
     */
    public DockerProxy_infoResponse dockerProxy_info(int environmentId) throws PortainerApiCallException, IOException {
        DockerProxyParams params = new DockerProxyParams();
        params.environmentId = environmentId;
        params.method = "GET";
        params.dockerAPIPath = "/info";

        ResponseParam responseParam = apiCall("dockerProxy", params);
        if(responseParam.isSucceed) {
            //成功時
            return new DockerProxy_infoResponse(responseParam.responseBody);
        } else {
            //失敗時
            System.err.println(responseParam.responseBody);
            throw new PortainerApiCallException("dockerProxy info API call failed.", responseParam);
        }
    }

    /*
    //確認用に使って下さい。
    #!/usr/bin/env bash
    TOKEN='[token masked]'
    BASE='http://example-mcp-proxy.local:8010'
    ENV_ID=9

      curl -s -X POST "$BASE/dockerProxy" \
        -H 'accept: application/json' \
        -H 'Content-Type: application/json' \
        -H "Authorization: Bearer $TOKEN" \
        -d "{
          \"environmentId\": $ENV_ID,
          \"method\": \"GET\",
          \"dockerAPIPath\": \"/containers/json\",
          \"queryParams\": [
            { \"key\": \"all\", \"value\": \"true\" }
          ]
        }"
     */
    public DockerProxy_containersResponse dockerProxy_containers(int environmentId) throws PortainerApiCallException, IOException {
        DockerProxyParams params = new DockerProxyParams();
        params.environmentId = environmentId;
        params.method = "GET";
        params.dockerAPIPath = "/containers/json";
        params.queryParams = new ArrayList<>();
        params.queryParams.add(new QueryParam("all", "true")); // 停止コンテナも取得

        ResponseParam responseParam = apiCall("dockerProxy", params);
        if(responseParam.isSucceed) {
            //成功時
            if (responseParam.responseBody != null && responseParam.responseBody.startsWith("\"")) {
                ObjectMapper mapper = new ObjectMapper();
                return new DockerProxy_containersResponse(mapper.readValue(responseParam.responseBody, String.class));
            }
            return new DockerProxy_containersResponse(responseParam.responseBody);
        } else {
            //失敗時
            System.err.println(responseParam.responseBody);
            throw new PortainerApiCallException("dockerProxy containers API call failed.", responseParam);
        }
    }

    /*
    //確認用に使って下さい。
    #!/usr/bin/env bash
    TOKEN='[token masked]'
    BASE='http://example-mcp-proxy.local:8010'
    ENV_ID=9
    CONTAINER_ID='11111111111111111111111111111111111111'

    curl -s -X POST "$BASE/dockerProxy" \
      -H 'accept: application/json' \
      -H 'Content-Type: application/json' \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"environmentId\": $ENV_ID,
        \"method\": \"GET\",
        \"dockerAPIPath\": \"/containers/$CONTAINER_ID/json\"
      }"
     */
    public DockerProxy_containerResponse dockerProxy_container(int environmentId, String containerId) throws PortainerApiCallException, IOException {
        DockerProxyParams params = new DockerProxyParams();
        params.environmentId = environmentId;
        params.method = "GET";
        params.dockerAPIPath = "/containers/" + containerId + "/json";

        ResponseParam responseParam = apiCall("dockerProxy", params);
        if(responseParam.isSucceed) {
            //成功時
            return new DockerProxy_containerResponse(responseParam.responseBody);
        } else {
            //失敗時
            System.err.println(responseParam.responseBody);
            throw new PortainerApiCallException("dockerProxy json API call failed.", responseParam);
        }
    }

    /*
    //確認用に使って下さい。
    #!/usr/bin/env bash
    TOKEN='[token masked]'
    BASE='http://example-mcp-proxy.local:8010'
    ENV_ID=9
    CONTAINER_ID='11111111111111111111111111111111111111'

    curl -s -X POST "$BASE/dockerProxy" \
      -H 'accept: application/json' \
      -H 'Content-Type: application/json' \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"environmentId\": $ENV_ID,
        \"method\": \"GET\",
        \"dockerAPIPath\": \"/containers/$CONTAINER_ID/stats\",
        \"queryParams\": [
          { \"key\": \"stream\", \"value\": \"false\" }
        ]
      }"
     */
    public DockerProxy_containerStatsResponse dockerProxy_containerStats(int environmentId, String containerId) throws PortainerApiCallException, IOException {
        DockerProxyParams params = new DockerProxyParams();
        params.environmentId = environmentId;
        params.method = "GET";
        params.dockerAPIPath = "/containers/" + containerId + "/stats";
        params.queryParams = new ArrayList<>();
        params.queryParams.add(new QueryParam("stream", "false")); // ★必須: 省略するとストリームが返り続ける

        ResponseParam responseParam = apiCall("dockerProxy", params);
        if(responseParam.isSucceed) {
            //成功時
            return new DockerProxy_containerStatsResponse(responseParam.responseBody);
        } else {
            //失敗時
            System.err.println(responseParam.responseBody);
            throw new PortainerApiCallException("dockerProxy stats API call failed.", responseParam);
        }
    }

    /*
    //確認用に使って下さい。
    #!/usr/bin/env bash
    TOKEN='[token masked]'
    BASE='http://example-mcp-proxy.local:8010'
    ENV_ID=9
    CONTAINER_ID='11111111111111111111111111111111111111'

    curl -s -X POST "$BASE/dockerProxy" \
      -H 'accept: application/json' \
      -H 'Content-Type: application/json' \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"environmentId\": $ENV_ID,
        \"method\": \"GET\",
        \"dockerAPIPath\": \"/containers/$CONTAINER_ID/logs\",
        \"queryParams\": [
          { \"key\": \"stdout\", \"value\": \"true\" },
          { \"key\": \"stderr\", \"value\": \"true\" },
          { \"key\": \"timestamps\", \"value\": \"true\" },
          { \"key\": \"tail\", \"value\": \"200\" }
        ]
      }"
     */
    public String dockerProxy_containerLogs(int environmentId, String containerId) throws PortainerApiCallException {
        DockerProxyParams params = new DockerProxyParams();
        params.environmentId = environmentId;
        params.method = "GET";
        params.dockerAPIPath = "/containers/" + containerId + "/logs";
        params.queryParams = new ArrayList<>();
        params.queryParams.add(new QueryParam("stdout",     "true")); // 標準出力を含める
        params.queryParams.add(new QueryParam("stderr",     "true")); // 標準エラーを含める
        params.queryParams.add(new QueryParam("timestamps", "true")); // タイムスタンプを付与
        params.queryParams.add(new QueryParam("tail",       "200"));  // 末尾から最大200行

        ResponseParam responseParam = apiCall("dockerProxy", params);
        if(responseParam.isSucceed) {
            //成功時
            return responseParam.responseBody;
        } else {
            //失敗時
            System.err.println(responseParam.responseBody);
            throw new PortainerApiCallException("dockerProxy logs API call failed.", responseParam);
        }
    }

}
