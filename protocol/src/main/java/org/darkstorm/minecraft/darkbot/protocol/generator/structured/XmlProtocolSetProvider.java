package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import org.darkstorm.minecraft.darkbot.protocol.generator.ProtocolSet;
import org.darkstorm.minecraft.darkbot.protocol.generator.ProtocolSetProvider;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.*;

public class XmlProtocolSetProvider implements ProtocolSetProvider {
    private interface ElementProcessor {
        public void process(Element element) throws ProtocolParseException;
    }

    private final ProtocolSet protocolSet;
    private final DefaultCodeProducer defaultCodeProducer;

    private final TypeModel typeTypeModel = new DefaultTypeModel();

    public XmlProtocolSetProvider(DefaultCodeProducer defaultCodeProducer, Reader familySource, Reader... protocolSources) {
        this.defaultCodeProducer = defaultCodeProducer;

    }

    private ProtocolFamily readFamily(Reader source) throws IOException, ProtocolParseException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(source);
            Element root = doc.getRootElement();
            if(!root.getName().equals("protocol-family"))
                throw new ProtocolParseException("Incorrect base name");

            String name = requireAttribute(root, "name").getValue();
            String script = requireAttribute(root, "script").getValue();

            Element headerElement = requireElement(root, "header");
            Element typesElement = root.getChild("types");

            Map<String, TypeModel> typeModels = new HashMap<>();
            if(typesElement != null) {
                for(Element child : typesElement.getChildren("type")) {
                    TypeModel typeModel = parseTypeModel(child, script);
                    if(typeModels.put(typeModel.getName(), typeModel) != null)
                        throw new ProtocolParseException("Duplicate type model name '" + typeModel.getName() + "'");

                }
            }
            Compound header = parseHeader(headerElement, typeModels);
        } catch(JDOMException exception) {
            throw new ProtocolParseException(exception);
        }
    }

    private Compound parseHeader(Element element, Map<String, TypeModel> typeModels) throws ProtocolParseException {

    }

    private TypeModel parseTypeModel(Element element, String sourceType) throws ProtocolParseException {
        String typeName = requireAttribute(element, "name").getValue();
        String typeClass = requireAttribute(element, "provides").getValue();

        List<TypeOption> options = new ArrayList<>();
        Element optionsElement = element.getChild("options");
        for(Element child : optionsElement.getChildren("option")) {
            String name = requireAttribute(child, "name").getValue();
            String type = requireAttribute(child, "type").getValue();
            String defaultValue = child.getAttributeValue("default");

            options.add(new DefaultTypeOption(name, type, defaultValue));
        }



        return new DefaultTypeModel(typeName, typeClass, options, constants, readCode, writeCode);
    }



    private Compound parseObjectCompound(Element element, Function<String, TypeModel> typeMap) throws ProtocolParseException {
        List<Field> fields = new ArrayList<>();

    }

    private Field parseField(Element element, Function<String, TypeModel> typeMap) throws ProtocolParseException {
        String name = requireAttribute(element, "name").getValue();
        Type type = parseType(element, typeMap);


    }

    private Compound parseCompound(Element element, String sourceType, Function<String, TypeModel> typeMap) throws ProtocolParseException {
        return parseCompound(element, sourceType, typeMap,
                defaultCodeProducer::produceCompoundReadCode, defaultCodeProducer::produceCompoundWriteCode);
    }

    private Compound parseCompound(Element element, String sourceType, Function<String, TypeModel> typeMap,
                                   Supplier<Code> defaultReadCode, Supplier<Code> defaultWriteCode) throws ProtocolParseException {
        List<Constant> constants = new ArrayList<>();
        processOptionalElementChildren(element, "constants", "constant", (child) -> {
            String name = requireAttribute(child, "name").getValue();
            String className = requireAttribute(child, "class").getValue();

            Code initCode = parseCode(child, sourceType);

            constants.add(new DefaultConstant(name, className, initCode));
        });

        Element importElement = element.getChild("import");
        Element readElement = element.getChild("read");
        Element writeElement = element.getChild("write");

        Code importCode = importElement == null ? new DefaultCode("", sourceType) : parseCode(importElement, sourceType);
        Code readCode = readElement == null ? defaultReadCode.get() : parseCode(readElement, sourceType);
        Code writeCode = writeElement == null ? defaultWriteCode.get() : parseCode(writeElement, sourceType);

        return new DefaultCompound(constants, importCode, readCode, writeCode);
    }

    private Type parseType(Element element, Function<String, TypeModel> typeMap) throws ProtocolParseException {
        String typeName = element.getAttributeValue("type");
        if(typeName != null) {
            if(element.getChild("type") != null)
                throw new ProtocolParseException("Multiple types defined for '" + describeElementPath(element) + "'");

            TypeModel model = typeMap.apply(typeName);
            if(model == null)
                throw new ProtocolParseException("Unknown type '" + typeName + "' on '" + describeElementPath(element) + "'");
            return model.createBuilder().build();
        }

        Element typeElement = requireElement(element, "type");
        typeName = requireAttribute(element, "name").getValue();
        TypeModel model = typeMap.apply(typeName);
        if(model == null)
            throw new ProtocolParseException("Unknown type '" + typeName + "' on '" + describeElementPath(element) + "'");

        TypeBuilder builder = model.createBuilder();
        for(Element child : typeElement.getChildren()) {
            String optionName = child.getName();
            TypeOption option = model.getOption(optionName);
            if(option == null)
                throw new ProtocolParseException("Unknown option '" + describeElementPath(child) + "'");
            TypeOptionValue optionValue = child.getText();
            builder = builder.withOption(option, optionValue);
        }
        return builder.build();
    }

    private void processOptionalElementChildren(Element parent, String name, String childName, ElementProcessor processor) throws ProtocolParseException {
        List<Element> elements = parent.getChildren(name);
        if(elements.size() > 1)
            throw new ProtocolParseException("Duplicate element '" + describeElementPath(parent) + "/" + name + "'");

        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            for(Element child : element.getChildren(childName))
                processor.process(child);
        }
    }

    private Code parseCode(Element element, String sourceType) throws ProtocolParseException {
        String elementSource = element.getText();
        String elementSourceType = element.getAttributeValue("script");
        if(elementSourceType == null)
            elementSourceType = sourceType;
        return new DefaultCode(elementSource, elementSourceType);
    }

    private Element requireElement(Element parent, String name) throws ProtocolParseException {
        List<Element> elements = parent.getChildren(name);
        if(elements.isEmpty())
            throw new ProtocolParseException("Element '" + describeElementPath(parent) + "/" + name + "' not specified");
        if(elements.size() > 1)
            throw new ProtocolParseException("Duplicate element '" + describeElementPath(parent) + "/" + name + "'");
        return elements.get(0);
    }

    private Attribute requireAttribute(Element element, String attributeName) throws ProtocolParseException {
        Attribute attribute = element.getAttribute(attributeName);
        if(attribute == null)
            throw new ProtocolParseException("Element '" + describeElementPath(element) + "' is missing attribute '" + attributeName + "'");
        return attribute;
    }

    private String describeElementPath(Element element) {
        StringBuilder path = new StringBuilder(element.getName());
        while(!element.isRootElement()) {
            element = element.getParentElement();
            StringBuilder builder = new StringBuilder();
            builder.append(element.getName()).append('{');
            List<Attribute> attributes = element.getAttributes();
            for(int i = 0; i < attributes.size(); i++) {
                if(i > 0)
                    builder.append(", ");

                Attribute attr = attributes.get(i);
                builder.append(attr.getName()).append('=').append(attr.getValue());
            }
            path.insert(0, builder.append("}/").toString());
        }
        return path.toString();
    }

    @Override
    public ProtocolSet getProtocolSet() {
        return protocolSet;
    }
}
