package com.hubnx.app;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Public, non-installable Android debug channel. No secrets or downloadable APK are embedded. */
public final class DebugBuildInfo {
    private static final String URL =
        "https://raw.githubusercontent.com/Wilianjcr/HubNX/main/update/android-debug.json";
    public final String version;
    public final String pageUrl;
    private DebugBuildInfo(String version, String pageUrl) {
        this.version = version;
        this.pageUrl = pageUrl;
    }

    public static DebugBuildInfo fetch() throws Exception {
        HttpURLConnection connection=(HttpURLConnection)new URL(URL).openConnection();
        connection.setRequestProperty("User-Agent","HubNX-Debug-Update");
        connection.setConnectTimeout(9000);
        connection.setReadTimeout(10000);
        connection.setUseCaches(false);
        try {
            if(connection.getResponseCode()==404)return null;
            if(connection.getResponseCode()!=200)
                throw new IllegalStateException("GitHub HTTP "+connection.getResponseCode());
            byte[] data;
            try(InputStream in=connection.getInputStream();
                ByteArrayOutputStream out=new ByteArrayOutputStream()){
                byte[] buf=new byte[4096];int n;
                while((n=in.read(buf))!=-1){
                    out.write(buf,0,n);
                    if(out.size()>12000)throw new IllegalArgumentException("Manifesto muito grande");
                }
                data=out.toByteArray();
            }
            JSONObject obj=new JSONObject(new String(data,StandardCharsets.UTF_8));
            String version=obj.optString("version");
            String url=obj.optString("run_url");
            if(!version.matches("[0-9]+\\.[0-9]+\\.[0-9]+"))return null;
            if(!url.matches("https://github\\.com/Wilianjcr/HubNX/actions/runs/[0-9]+"))return null;
            if(!"debug".equals(obj.optString("channel")))return null;
            return new DebugBuildInfo(version,url);
        }finally{connection.disconnect();}
    }
    public boolean isNewer(){
        return ReleaseInfo.compare(version,BuildConfig.VERSION_NAME)>0;
    }
}
