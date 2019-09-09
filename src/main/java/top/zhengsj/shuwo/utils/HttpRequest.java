package top.zhengsj.shuwo.utils;

import com.squareup.okhttp.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class HttpRequest {
    public JSONObject sendPost(String url, Map<String, Object> content) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
        MultipartBuilder postBodyBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        for (Map.Entry<String, Object> item : content.entrySet()) {
            postBodyBuilder.addPart(
                    Headers.of("Content-Disposition", "form-data; name=\"" + item.getKey() + "\""),
                    RequestBody.create(MEDIA_TYPE_TEXT, item.getValue().toString()));
        }
        RequestBody postBody = postBodyBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(postBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }

        return new JSONObject(response.body().string());
    }

    public JSONObject sendGet(String url, Map<String, Object> content) throws IOException {
        OkHttpClient client = new OkHttpClient();

        StringBuilder urlBuilder = new StringBuilder(url);

        boolean isFirst = true;
        for (Map.Entry<String, Object> item : content.entrySet()) {
            urlBuilder.append(isFirst ? "?" : "&")
                    .append(item.getKey()).append("=").append(item.getValue());
            isFirst = false;
        }
        url = urlBuilder.toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }

        return new JSONObject(response.body().string());
    }
}
