package com.worksap.kryotest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.minlog.Log;

public class KryoTest {

    final static Logger logger = Logger.getLogger(KryoTest.class);

    /**
     * 
     * @param args
     */

    public static void main(String[] args) {

        // Basic Kryo Instance
        Kryo kryo = new Kryo();
        Log.ERROR();
        logger.info("----------------------------------------------------------------");
        logger.info("             Class                  | Pattern  |   Size   | Can ");
        logger.info("----------------------------------------------------------------");
        testSerializer(kryo, CompatibleFieldSerializer.class);
        testSerializer(kryo, FieldSerializer.class);

    }

    @SuppressWarnings("rawtypes")
    private static void testSerializer(Kryo kryo, Class<? extends Serializer> serializer) {
        logger.info(StringUtils.center(serializer.getSimpleName(), 65));
        kryo.setDefaultSerializer(serializer);
        
        TransientField transientObject = new TransientField();
        transientObject.transientInt = 90;
        transientObject.myInt = 90;

        testObject(kryo, transientObject);
        
        StaticField staticObject = new StaticField();
        staticObject.myInt = 90;

        testObject(kryo, staticObject);
        
        logger.info("----------------------------------------------------------------");
    }

    private static void testObject(Kryo kryo, Object object) {

        testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.OBJECT);

        testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.CLASSnOBJECT);

        testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.OBJECT);

        testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.CLASSnOBJECT);
    }

    private static void testDifferentPatterns(Kryo kryo, Object object, Pattern read, Pattern write) {

        // For Logging;
        long size = -1;
        char serialized = 'U';

        byte[] outputBytes = null;
        // Stream to collect object; closed automatically.
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

        try (Input input = new Input(outputBytes)) {

            // Reading
            Object returnObject = null;
            if (write.equals(Pattern.OBJECT)) {
                returnObject = kryo.readObject(input, object.getClass());

            } else if (write.equals(Pattern.CLASSnOBJECT)) {
                returnObject = kryo.readClassAndObject(input);
            }
            serialized = object.toString().compareTo(returnObject.toString()) == 0 ? 'Y' : 'N';
        }
        String line = String.format("%s | %s | %8d | %2s",
                StringUtils.center(object.getClass().getSimpleName(), 35),
                StringUtils.center(read.ordinal() + "-" + write.ordinal(), 8), size,
                serialized);
        logger.info(line);

    }

    enum Pattern {
        OBJECT, CLASSnOBJECT
    }

}
