package com.mingyuans.smoke;

/*****************************************************************************
 Copyright mingyuans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/


import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by yanxq on 17/3/2.
 */

public class StringUtil {

    private enum Format {
        Null {
            @Override
            public boolean instanceOf(Object arg) {
                return arg == null;
            }

            @Override
            public String toString(Object arg) {
                return "null";
            }
        },

        Array {
            @Override
            public boolean instanceOf(Object arg) {
                return arg instanceof Object[];
            }

            @Override
            public String toString(Object arg) {
                return array2String((Object[]) arg);
            }
        },

        Collection {
            @Override
            public boolean instanceOf(Object arg) {
                return arg instanceof Collection;
            }

            @Override
            public String toString(Object arg) {
                return collection2String((java.util.Collection) arg);
            }
        },

        Json {
            @Override
            public boolean instanceOf(Object arg) {
                if (arg instanceof String) {
                    String str = (String)arg;
                    return str.trim().startsWith("{");
                }
                return false;
            }

            @Override
            public String toString(Object arg) {
                return json2String((String) arg);
            }
        },

        Xml {
            @Override
            public boolean instanceOf(Object arg) {
                if (arg instanceof String) {
                    String str = (String)arg;
                    return str.trim().startsWith("<");
                }
                return false;
            }

            @Override
            public String toString(Object arg) {
                return xml2String((String) arg);
            }
        }
        ;

        public boolean instanceOf(Object arg) {
            return false;
        }

        public String toString(Object arg) {
            return "";
        }
    }

    public static String toString(Object arg) {
        String message = "";
        for( Format format : Format.values()) {
            if (format.instanceOf(arg)) {
                message = format.toString(arg);
            }
        }

        if (StringUtil.isEmpty(message)) {
            message = String.valueOf(arg);
        }
        return message;
    }

    public static String json2String(String json) {
        final int INTENT = 1;
        try {
            if (!isEmpty(json)) {
                return formatJsonString(json,INTENT);
            }
        } catch (Throwable throwable) {

        }
        return json;
    }

    public static String xml2String(String xml) {
        if (!isEmpty(xml)) {
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
            } catch (TransformerException e) {

            }
        }

        return xml;
    }

    public static String collection2String(Collection collection) {
        StringBuilder builder = new StringBuilder("[");
        for (Object child : collection) {
            builder.append(toString(child));
            builder.append(",");
        }
        if (collection.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }

    public static String array2String(Object[] arrayObject) {
        String message = "";
        if (arrayObject == null) {
            return message;
        }

        StringBuilder builder = new StringBuilder("[");
        int length = arrayObject.length;
        for (int i = 0; i < length; i++) {
            builder.append(toString(arrayObject[i]));
            if (i == (length - 1)) {
                builder.append("]");
            } else {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public static String format(String message, Object... args) {
        if (message.indexOf("%") == -1) {
            // {} or {0},{1}...
            String[] strObjects = new String[0];
            if (args != null && args.length > 0) {
                strObjects = new String[args.length];
                for (int i = 0, length = args.length; i < length; i++) {
                    strObjects[i] = toString(args[i]);
                }
            }
            return MessageFormat.format(message, (Object[]) strObjects);
        } else {
            return String.format(message, args);
        }
    }

    public static String getSimpleName(String className) {
        int lastIndex = className.lastIndexOf(".");
        if (lastIndex == -1) {
            return className;
        } else {
            return className.substring(lastIndex+1);
        }
    }

    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    public static String throwable2String(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        Throwable lastCause = throwable;
        while (lastCause.getCause() != null) {
            lastCause = lastCause.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        lastCause.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static String formatJsonString(String json,int intentStep) {
        if (json == null || json.trim().isEmpty()) {
            return json;
        }

        int intentCount = 0;
        char current = '\0',before = '\0';
        StringBuilder builder = new StringBuilder();
        for (int index = 0,length=json.length(); index < length; index++) {
            before = current; current = json.charAt(index);
            if (current == '{' || current == '[') {
                builder.append(current);
                builder.append('\n');
                intentCount += intentStep;
                appendIntent(builder,intentCount);
            } else if (current == '}' || current == ']') {
                builder.append('\n');
                intentCount -= intentStep;
                appendIntent(builder,intentCount);
                builder.append(current);
            } else if (current == ',') {
                builder.append(current);
                if (before != '\\') {
                    builder.append('\n');
                    appendIntent(builder,intentCount);
                }
            } else {
                builder.append(current);
            }
        }
        return builder.toString();
    }

    private static void appendIntent(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.append("\t");
        }
    }
}
