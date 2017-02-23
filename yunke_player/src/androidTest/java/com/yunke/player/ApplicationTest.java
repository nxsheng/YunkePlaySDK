package com.yunke.player;

import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<AppContext> {

    private AppContext application;
    public ApplicationTest() {
        super(AppContext.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // 获取application之前必须调用的方法
        createApplication();
        application = getApplication();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSetObj() {
        LoginResult user = new LoginResult();
        user.code = 1;
        user.message = "success";
        AppContext.set("user", user);
    }

    public void testGetObj() {
        LoginResult user = (LoginResult) AppContext.get("user", new Object());
        assertNotNull(user);
        Log.d("wangyanan", user.code + ", " + user.message);
    }

    public void testDB_Insert() {
        TestDataBase testDataBase = new TestDataBase(getContext());
        Test t = new Test();
        t._id = 0;
        t.test_id = 0;
        t.test_title = "test";
        testDataBase.insert(t);
    }

    public void testDB_Update() {
        TestDataBase testDataBase = new TestDataBase(getContext());
        Test t = new Test();
        t._id = 0;
        t.test_id = 1;
        t.test_title = "test1";
        testDataBase.update(t);
    }

    public void testDB_Delete() {
        TestDataBase testDataBase = new TestDataBase(getContext());
        testDataBase.delete(0);
    }

    public void testDB_Query() {
        TestDataBase testDataBase = new TestDataBase(getContext());
        List<Test> datas = testDataBase.query(" where _id=0 ");
        assertEquals(true, !datas.isEmpty());
    }

    public void testDB_Save() {
        TestDataBase testDataBase = new TestDataBase(getContext());
        Test t = new Test();
        t._id = 0;
        t.test_id = 1;
        t.test_title = "test1";
        testDataBase.save(t);
        testDB_Query();
    }

    public void testDB_Reset() {
        TestDataBase testDataBase = new TestDataBase(getContext());
        testDataBase.reset(new ArrayList<Test>());
    }

    public void testSetProperties() {
        final User user = new User();
        user.uid = 1;
        user.name = "name";
        user.username = "13001111269";
        user.password = "123456";
        user.token = "ffb0e5ec8ae6889d5791c7cd49cb3afb";
        user.large = "l";
        user.medium = "m";
        user.small = "s";
        application.setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.uid));
                setProperty("user.name", user.name);
                setProperty("user.large", user.large);
                setProperty("user.medium", user.medium);
                setProperty("user.small", user.small);
                setProperty("user.username", user.username);
                setProperty("user.password",
                        CyptoUtil.encode("gn100App", user.password));
                setProperty("user.token", user.token);
            }
        });
    }
}