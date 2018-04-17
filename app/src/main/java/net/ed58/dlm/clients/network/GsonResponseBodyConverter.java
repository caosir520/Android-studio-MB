package net.ed58.dlm.clients.network;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.ed58.dlm.clients.network.entity.HttpResult;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by sunpeng on 16/11/4.
 */

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
        int a[] ={1,2,3,4,5};
        for (int i = 0; i < a.length-1; i++) {
            int a1 = a[i];
        }

    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader reader = value.charStream();

        try {
//            Response httpResult = gson.fromJson(value.toString(), Response.class);

            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);
            T t = gson.fromJson(jsonReader, type);
            HttpResult httpResult = (HttpResult) t;
            if ("0".equals(httpResult.getCode())) {
                return t;
            } else {
                throw new ResultException(Integer.parseInt(httpResult.getCode()), httpResult.getMsg());
            }

        } finally {
            closeQuietly(reader);
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}