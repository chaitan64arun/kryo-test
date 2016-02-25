package com.worksap.kryotest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.minlog.Log;
import com.worksap.kryotest.classes.CollectionClass;
import com.worksap.kryotest.classes.ComplexClass;
import com.worksap.kryotest.classes.ExtendingClass;
import com.worksap.kryotest.classes.ExtendingRepeatClass;
import com.worksap.kryotest.classes.GenericClass;
import com.worksap.kryotest.classes.MyImplementingClass;
import com.worksap.kryotest.classes.NullPrimitiveClass;
import com.worksap.kryotest.classes.OnlyPrimitiveClass;
import com.worksap.kryotest.classes.StaticField;
import com.worksap.kryotest.classes.TransientField;
import com.worksap.kryotest.classes.heavy.HeavyClass;
import com.worksap.kryotest.classes.heavy.NullHeavyClass;
import com.worksap.kryotest.classes.heavy.PrivateHeavyClass;
import com.worksap.kryotest.classes.heavy.StaticHeavyClass;
import com.worksap.kryotest.classes.heavy.StaticNullHeavyClass;
import com.worksap.kryotest.helper.MyAbstractClass;
import com.worksap.kryotest.helper.MyInterface;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;

public class KryoBasicTest {

    private static final String SEPARATOR = "-----------------------------------------------------------------------------------------------";
    final static Logger logger = Logger.getLogger(KryoBasicTest.class);

    @SuppressWarnings("rawtypes")
    Class<? extends Serializer> currentSerializer = null;

    public static void main(String[] args) {

        Log.ERROR();
        KryoBasicTest test = new KryoBasicTest();
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

        testBasic();

        MyAbstractClass extendedRepeatObject = new ExtendingRepeatClass();
        extendedRepeatObject.changeValues();
        testObject(extendedRepeatObject);

        MyAbstractClass extendedObject = new ExtendingClass();
        extendedObject.changeValues();
        testObject(extendedObject);

        MyInterface myImplementingClass = new MyImplementingClass();
        ((MyImplementingClass)myImplementingClass).changeValues();
        testObject(myImplementingClass);

        GenericClass<Integer> intGeneric = new GenericClass<>();
        intGeneric.changeValues(1234);
        testObject(intGeneric);

        GenericClass<MyAbstractClass> abstractGeneric = new GenericClass<>();
        abstractGeneric.changeValues(extendedObject);
        testObject(abstractGeneric);

        GenericClass<MyInterface> interfaceGeneric = new GenericClass<>();
        interfaceGeneric.changeValues(myImplementingClass);
        testObject(interfaceGeneric);

        CollectionClass collectionClass = new CollectionClass();
        collectionClass.changeValues(Arrays.asList(1256, 53, 866),
                Arrays.asList(myImplementingClass, interfaceGeneric), Arrays.asList(intGeneric, extendedObject));
        testObject(collectionClass);

        ComplexClass<MyAbstractClass> complexClass = new ComplexClass<>();
        complexClass.changeValues(1983342, collectionClass, abstractGeneric,
                new ImmutablePair<>(12313, extendedObject), extendedObject);
        testObject(complexClass);

        OnlyPrimitiveClass onlyPrimitiveClass = new OnlyPrimitiveClass();
        onlyPrimitiveClass.changeValues();
        testObject(onlyPrimitiveClass);

        NullPrimitiveClass nullPrimitiveClass = new NullPrimitiveClass();
        nullPrimitiveClass.changeValues();
        testObject(nullPrimitiveClass);

        testHeavyPattern();

    }

    private void testBasic() {
        TransientField transientObject = new TransientField();
        transientObject.changeValues();
        testObject(transientObject);

        StaticField staticObject = new StaticField();
        staticObject.changeValues();
        testObject(staticObject);
    }

    private void testHeavyPattern() {
        HeavyClass heavyClass = new HeavyClass();
        heavyClass.changeValues();
        testObject(heavyClass);

        NullHeavyClass nullHeavyClass = new NullHeavyClass();
        nullHeavyClass.changeValues();
        testObject(nullHeavyClass);

        StaticHeavyClass staticHeavyClass = new StaticHeavyClass();
        StaticHeavyClass.changeValues();
        testObject(staticHeavyClass);

        StaticNullHeavyClass staticNullHeavyClass = new StaticNullHeavyClass();
        staticNullHeavyClass.changeValues();
        testObject(staticNullHeavyClass);

        PrivateHeavyClass privateHeavyClass = new PrivateHeavyClass();
        privateHeavyClass.changeValues();
        testObject(privateHeavyClass);
    }

    @SuppressWarnings("rawtypes")
    public void testSerializer(Class<? extends Serializer> serializer, Object object) {
        currentSerializer = serializer;
        
        Kryo kryo = getKryo();

        kryo.setDefaultSerializer(serializer);

        testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.OBJECT);

        // testDifferentPatterns(kryo, object, Pattern.OBJECT, Pattern.CLASSnOBJECT);

        // testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.OBJECT);

        testDifferentPatterns(kryo, object, Pattern.CLASSnOBJECT, Pattern.CLASSnOBJECT);
        logger.info(SEPARATOR);
    }

    public void testObject(Object object) {
        testSerializer(CompatibleFieldSerializer.class, object);
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
