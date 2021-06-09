package flower.workflow.impl;

import flower.workflow.DependencyWorkFlow;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MapDependencyWorkFlow extends DependencyWorkFlow {

    String ENGINE = "engine" ;

    String NAME = "name";

    String PARAMS = "params";

    String NODES = "params";

    interface MapFNode extends DependencyWorkFlow.FNode {

        String WHEN = "when" ;

        String BODY = "body" ;

        String OWNER = "owner" ;

        String DEPENDS = "depends" ;

        Map<String,Object> config();

        @Override
        default String name() {
            return (String) config().getOrDefault(NAME, "");
        }

        @Override
        default Map<String, Object> params() {
            return (Map) config().getOrDefault(PARAMS, Collections.emptyMap());
        }

        @Override
        default Predicate<Map<String, Object>> when() {
            DynamicExecution e = DynamicExecution.engine( (String) config().getOrDefault(ENGINE,""));
            return e.predicate((String) config().getOrDefault(WHEN,"") );
        }

        @Override
        default Function<Map<String, Object>, Object> body() {
            DynamicExecution e = DynamicExecution.engine( (String) config().getOrDefault(ENGINE,""));
            return e.function((String) config().getOrDefault(BODY,"") );
        }

        @Override
        default DependencyWorkFlow owner() {
            return null;
        }

        @Override
        default Set<String> dependencies() {
            List<String> l = (List)config().getOrDefault(DEPENDS,Collections.emptyList());
            return new HashSet<>(l);
        }
    }

    Map<String,Object> config();

    @Override
    default String name() {
        return (String) config().getOrDefault(NAME, "");
    }

    @Override
    default Map<String, FNode> nodes() {
        Map nodeData = (Map)config().getOrDefault( NODES, Collections.emptyMap());
        for ( Object k : nodeData.keySet()){
            Object v = nodeData.get(k);
            if ( v instanceof FNode ) break;
            Map<String,Object> nodeConfig = (Map)v;
            nodeConfig.put(NAME, k);
            MapFNode mapFNode = () -> nodeConfig;
            nodeData.put(k, mapFNode);
        }
        return (Map<String, FNode>)nodeData;
    }

    @Override
    default Map<String, Object> params() {
        return (Map)config().getOrDefault( PARAMS, Collections.emptyMap());
    }
}