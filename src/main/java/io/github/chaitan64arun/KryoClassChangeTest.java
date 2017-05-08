package io.github.chaitan64arun;

import io.github.chaitan64arun.classes.ExtendingClass;
import io.github.chaitan64arun.helper.MyAbstractClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.minlog.Log;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;

public class KryoClassChangeTest {

    private static final String SEPARATOR = "-----------------------------------------------------------------------------------------------";
    final static Logger logger = Logger.getLogger(KryoClassChangeTest.class);

    @SuppressWarnings("rawtypes")
    Class<? extends Serializer> currentSerializer = null;
    
    private byte[] outputBytes = null;

    public static void main(String[] args) {

        Log.ERROR();
        KryoClassChangeTest test = new KryoClassChangeTest();
        test.run();
    }

    Kryo getKryo() {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        return kryo;

    }

    public void run() {
        logger.info(SEPARATOR);
        logger.info("             Class                  | Pattern  |          Serializer          |   Size   | Can ");
        logger.info(SEPARATOR);

        MyAbstractClass extendedObject = new ExtendingClass();
        extendedObject.changeValues();
        testObject(extendedObject);
    }

    @SuppressWarnings("rawtypes")
    public void testSerializer(Class<? extends Serializer> serializer, Object object) {
        currentSerializer = serializer;

        Kryo kryo = getKryo();

        kryo.setDefaultSerializer(serializer);

        //testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.OBJECT);
        
        testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.CLASSnOBJECT);
        
        logger.info(SEPARATOR);
    }

    public void testObject(Object object) {
        //testSerializer(CompatibleFieldSerializer.class, object);
        testSerializer(FieldSerializer.class, object);
    }

    private void testDifferentPatterns(Kryo kryo, Object object, Pattern read, Pattern write) {

        // For Logging;
        long size = -1;
        char serialized = 'U';

        size = writeData(kryo, object, write, size);
        
        // Try adding new field
        String className = object.getClass().getName();
        
        int initialCount = getFieldCount(object.getClass());
        
        CtClass classInRef;
        try {
            classInRef = ClassPool.getDefault().get(className);
            CtField myNewField = CtField.make("public int bobTheBuilder = 0;", classInRef);
            classInRef.addField(myNewField);
        } catch (NotFoundException | CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        int finalCount = getFieldCount(object.getClass());
        
        logger.info(initialCount + "--" + finalCount);
        
        serialized = readData(kryo, object, write);
        String line = String.format("%s | %s | %s |%8d | %2s",
                StringUtils.center(object.getClass().getSimpleName(), 35),
                StringUtils.center(read.ordinal() + "-" + write.ordinal(), 8),
                StringUtils.center(currentSerializer.getSimpleName(), 28),
                size,
                serialized);
        
        if (serialized != 'Y') {
            logger.error(line);
        } else {
            logger.info(line);
        }

    }
    
    private int getFieldCount(Class clazz){
        List<Field> fieldList = new ArrayList<Field>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz .getDeclaredFields()));
            clazz = clazz .getSuperclass();
        }
        return fieldList.size();
    }

    private char readData(Kryo kryo, Object object, Pattern write) {
        char serialized;
        try (Input input = new Input(outputBytes)) {

            // Reading
            Object returnObject = null;
            if (write.equals(Pattern.OBJECT)) {
                returnObject = kryo.readObject(input, object.getClass());

            } else if (write.equals(Pattern.CLASSnOBJECT)) {
                returnObject = kryo.readClassAndObject(input);
            }
            logger.info(object + "##"+ returnObject);

            serialized = object.toString().compareTo(returnObject.toString()) == 0 ? 'Y' : 'N';
        }
        return serialized;
    }

    private long writeData(Kryo kryo, Object object, Pattern write, long size) {
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Output output = new Output(baos)) {

            if (write.equals(Pattern.OBJECT)) {
                kryo.writeObject(output, object);

            } else if (write.equals(Pattern.CLASSnOBJECT)) {
                kryo.writeClassAndObject(output, object);
            }
            size = output.total();
            outputBytes = output.toBytes();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    enum Pattern {
        OBJECT, CLASSnOBJECT
    }

}
