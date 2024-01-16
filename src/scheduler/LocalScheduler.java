package scheduler;

import parser.Node;
import parser.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocalScheduler {
    public static Map<Node, List<Node>> graph = new HashMap<>();

    void addNode(Node node, List<Node> dependencies) {
        graph.put(node, dependencies);
    }

    void executeTasks() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        while (!allTasksCompleted()) {
            for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
                Node node = entry.getKey();
//                System.out.println(node.getStatus());
                if (canBeExecuted(node)) {
                    node.status = TaskStatus.IN_PROGRESS;
                    executor.submit(node::execute);
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    boolean allTasksCompleted() {
        return graph.keySet().stream().allMatch(n -> n.status == TaskStatus.FINISHED);
    }

    boolean canBeExecuted(Node node) {
        if (node.status != TaskStatus.NOT_STARTED) {
            return false;
        }
        return graph.get(node).stream().allMatch(dep -> dep.status == TaskStatus.FINISHED);
    }
}