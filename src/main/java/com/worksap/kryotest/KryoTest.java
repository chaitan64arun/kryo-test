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
import com.worksap.kryotest.helper.MyAbstractClass;

public class KryoTest {

    private static final String SEPARATOR = "-----------------------------------------------------------------------------------------------";
    final static Logger logger = Logger.getLogger(KryoTest.class);
    
    // Basic Kryo Instance
    private static Kryo kryo = new Kryo();

    @SuppressWarnings("rawtypes")
    Class<? extends Serializer> currentSerializer = null;
    
    public static void main(String[] args) {

        Log.ERROR();
        KryoTest test = new KryoTest();
        test.run();
        //test.testSerializer(kryo, CompatibleFieldSerializer.class);
        //test.testSerializer(kryo, FieldSerializer.class);

    }
    
    public void run(){
        
        logger.info(SEPARATOR);
        logger.info("             Class                  | Pattern  |          Serializer          |   Size   | Can ");
        logger.info(SEPARATOR);
        
        TransientField transientObject = new TransientField();
        transientObject.changeValues();
        testObject( transientObject);

        StaticField staticObject = new StaticField();
        staticObject.changeValues();
        testObject( staticObject);
        MyAbstractClass extendedRepeatObject = new ExtendingRepeatClass();
        extendedRepeatObject.changeValues();
        testObject( extendedRepeatObject);

        MyAbstractClass extendedObject = new ExtendingClass();
        extendedObject.changeValues();
        testObject( extendedObject);
        
    }

    @SuppressWarnings("rawtypes")
    public void testSerializer(Class<? extends Serializer> serializer, Object object) {
        currentSerializer = serializer;
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

        testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.OBJECT);

        testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.CLASSnOBJECT);

        testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.OBJECT);

        testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.CLASSnOBJECT);
        logger.info(SEPARATOR);
    }

    public void testObject(Object object) {
        testSerializer( CompatibleFieldSerializer.class, object);
        testSerializer(FieldSerializer.class, object);
    }

    private void testDifferentPatterns(Kryo kryo, Object object, Pattern read, Pattern write) {

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
            // logger.info(object + "##"+ returnObject);

            serialized = object.toString().compareTo(returnObject.toString()) == 0 ? 'Y' : 'N';
        }
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

    enum Pattern {
        OBJECT, CLASSnOBJECT
    }

}
