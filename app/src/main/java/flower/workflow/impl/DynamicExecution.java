package flower.workflow.impl;

import static zoomba.lang.core.interpreter.ZContext.*;
import static zoomba.lang.core.operations.Function.MonadicContainer;

import zoomba.lang.core.interpreter.ZScript;
import zoomba.lang.core.types.ZTypes;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface DynamicExecution {

    Function<Map<String,Object>,Object> function(String s);

    Predicate<Map<String,Object>> predicate(String s);

    String engine();

    DynamicExecution DUMMY = new DynamicExecution() {
        //TODO this is for dry running and logging workflow
        @Override
        public Function<Map<String, Object>, Object> function(String s) {
            return stringObjectMap -> s ;
        }

        @Override
        public Predicate<Map<String, Object>> predicate(String s) {
            return stringObjectMap -> true ;
        }

        @Override
        public String engine() {
            return "dummy";
        }
    };

    DynamicExecution ZMB = new DynamicExecution() {

        @Override
        public Function<Map<String, Object>, Object> function(String s) {
            return new Function<>() {
                final ZScript script = new ZScript(s);

                @Override
                public Object apply(Map<String, Object> input) {
                    FunctionContext ctx =
                            new FunctionContext(EMPTY_CONTEXT, ArgContext.EMPTY_ARGS_CONTEXT);
                    input.forEach(ctx::set);
                    script.runContext(ctx);
                    MonadicContainer mc = script.execute();
                    return mc.value();
                }
            };
        }

        @Override
        public Predicate<Map<String, Object>> predicate(String s) {
            return new Predicate<>() {
                final ZScript script = new ZScript(s);

                @Override
                public boolean test(Map<String, Object> input) {
                    FunctionContext ctx =
                            new FunctionContext(EMPTY_CONTEXT, ArgContext.EMPTY_ARGS_CONTEXT);
                    input.forEach(ctx::set);
                    script.runContext(ctx);
                    MonadicContainer mc = script.execute();
                    return ZTypes.bool(mc.value(),false);
                }
            };
        }

        @Override
        public String engine() {
            return "zmb";
        }
    };
    
    
    static DynamicExecution engine(String name){
        switch ( name ){
            case "zmb" : return ZMB;
            default:
                return DUMMY;
        }
    }
}
