package testcase;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServiceTestCaseCache {

    public static LoadingCache<String, Map<String, List<RequestTestCase>>> SERVICE_TEST_CASES_DATA_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, Map<String, List<RequestTestCase>>>() {

                public Map<String, List<RequestTestCase>> load(String key) throws Exception {
                    return null;
                }
            });

}
