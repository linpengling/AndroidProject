/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wslerz.baselibrary.mvvm.http.converterFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.wslerz.baselibrary.mvvm.http.HttpConstant;
import com.wslerz.baselibrary.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String originalBody = value.string();

            // 解密
            // 获取json中的code，对json进行预处理
            JSONObject json = new JSONObject(originalBody);
            int code = json.optInt("code");
            // 当code不为CODE_SUCCESS时，设置data为{}，这样转化就不会出错了
            if (code != HttpConstant.CODE_SUCCESS) {
                if (json.has("data")) {
                    json.remove("data");
                }
                originalBody = json.toString();
            }
            return adapter.fromJson(originalBody);
        } catch (JSONException e) {
//            LogUtils.Companion.getInstance().i("end e = " + e);
            throw new RuntimeException(e.getMessage());
        } finally {
            value.close();
        }
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
//        try {
//            T result = adapter.read(jsonReader);
//            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw new JsonIOException("JSON document was not fully consumed.");
//            }
//            return result;
//        } finally {
//            value.close();
//        }
    }
}