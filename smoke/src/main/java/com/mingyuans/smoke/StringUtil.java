package com.mingyuans.smoke;

import android.text.TextUtils;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * Created by yanxq on 17/3/2.
 */

class StringUtil {


    private enum Format {
        Null {
            @Override
            public boolean match(Object arg) {
                return arg == null;
            }

            @Override
            public String toString(Object arg) {
                return "null";
            }
        },

        Array {
            @Override
            public boolean match(Object arg) {
                return arg instanceof Object[];
            }

            @Override
            public String toString(Object arg) {
                return array2String((Object[]) arg);
            }
        },

        Collection {
            @Override
            public boolean match(Object arg) {
                return arg instanceof Collection;
            }

            @Override
            public String toString(Object arg) {
                return collection2String((java.util.Collection) arg);
            }
        },

        String {
            @Override
            public boolean match(Object arg) {
                return arg instanceof String;
            }

            @Override
            public String toString(Object arg) {
                //Please call trim by your self!
                //String json = ((String)arg).trim();
                String json = (String) arg;
                if ((json.startsWith("{") && json.endsWith("}"))) {
                    //is json ?
                    return json2String((String) arg);
                }
                return super.toString(arg);
            }
        }

        ;

        public boolean match(Object arg) {
            return false;
        }

        public String toString(Object arg) {
            return "";
        }
    }

    public static String toString(Object arg) {
        String message = "";
        for( Format format : Format.values()) {
            if (format.match(arg)) {
                message = format.toString(arg);
            }
        }

        if (TextUtils.isEmpty(message)) {
            message = String.valueOf(arg);
        }
        return message;
    }

    public static String json2String(String json) {
        final int INTENT = 2;
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.toString(INTENT);
        } catch (Throwable throwable) {

        }
        return "";
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
        if (message.contains("{0}")) {
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
}
