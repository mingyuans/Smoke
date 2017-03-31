package com.mingyuans.smoke;

/**
 * Created by yanxq on 2017/3/2.
 */

public interface ISmoke {

    public void verbose();

    public void verbose(Object object);

    public void verbose(String message, Object... args);

    public void debug();

    public void debug(Object object);

    public void debug(String message, Object... args);

    public void info();

    public void info(Object object);

    public void info(String message, Object... args);

    public void warn();

    public void warn(Object object);

    public void warn(String message, Object... args);

    public void warn(Throwable throwable, String message, Object... args);

    public void error();

    public void error(Object object);

    public void error(String message, Object... args);

    public void error(Throwable throwable, String message, Object... args);

    public void log(int level, String tag, String message, Object... args);

    public void xml(int level, String tag, String xml);

    public void xml(int level, String xml);

    public void json(int level, String tag, String json);

    public void json(int level, String json);

}
