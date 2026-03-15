package io.github.bindglam.nationwar.core;

import io.github.bindglam.nationwar.Context;
import io.github.bindglam.nationwar.Managerial;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class CoreManager implements Managerial {
    private final Map<String, Core> cores = new LinkedHashMap<>();

    private Context context;

    private CoreRepository repository;

    @Override
    public void start(Context context) {
        this.context = context;

        repository = new CoreRepository(context.plugin());

        repository.createTable().join();
        loadAll();
    }

    @Override
    public void end(Context context) {
        saveAll(false);
    }

    private void loadAll() {
        context.logger().info("신상 데이터 로드 중...");

        repository.loadAll().thenAccept(it -> {
            cores.putAll(it);

            context.logger().info("신상 데이터 로드 완료!");
        });
    }

    public void saveAll(boolean async) {
        context.logger().info("신상 데이터 저장 중... 서버를 끄지 마세요.");

        var tasks = CompletableFuture.allOf(cores.values().stream().map(repository::save).toList().toArray(new CompletableFuture[0]));
        if(async) {
            tasks.thenRun(() ->
                    context.logger().info("신상 데이터 저장 완료!"));
        } else {
            tasks.join();
            context.logger().info("신상 데이터 저장 완료!");
        }
    }

    public boolean registerCore(Core core) {
        if(cores.containsKey(core.getName()))
            return false;
        cores.put(core.getName(), core);
        return true;
    }
}
