package org.academiadecodigo.asciimos.httpserver.server.cache;

import org.academiadecodigo.asciimos.httpserver.server.Response;

import java.util.ArrayList;
import java.util.List;

public class CacheList {

    private static List<Cache> cacheList = new ArrayList<Cache>();

    public static Response find(String path) {
        for (Cache cache : cacheList) {
            if(cache.getPath().equals(path)) {
                return cache.getResponse();
            }
        }
        return null;
    }

    public static void add(String path, Response response) {
        cacheList.add(new Cache(path, response));
    }

}
