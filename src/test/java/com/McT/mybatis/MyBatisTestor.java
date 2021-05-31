package com.McT.mybatis;

import com.McT.mybatis.dto.GoodsDTO;
import com.McT.mybatis.entity.Goods;
import com.McT.mybatis.entity.GoodsDetail;
import com.McT.mybatis.entity.Student;
import com.McT.mybatis.utils.myBatisUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.*;

public class MyBatisTestor {
    @Test
    public void testSqlSessionTestor() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        System.out.println("ha");

        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection connection = sqlSession.getConnection();
        System.out.println(connection);
    }

    @Test
    public void testMyBatisUtils()throws Exception{
        SqlSession sqlSession = null;
        try {
            sqlSession = myBatisUtils.openSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectAll()throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            List<Goods> list = session.selectList("goods.selectAll");
            for (Goods g: list){
                System.out.println(g.getTitle());
            }
        }catch (Exception e){
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectById()throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 740);
            System.out.println(goods.getTitle());
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectByPriceRange()throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            Map map = new HashMap();
            map.put("min",100);
            map.put("max", 300);
            map.put("limit", 10);
            List<Goods> list = session.selectList("selectByPriceRange", map);
            for (Goods g: list){
                System.out.println(g.getGoodsId()+" "+g.getTitle() + " "+g.getCurrentPrice());
            }
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectGoodsMap()throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            List<Map> list = session.selectList("selectGoodsMap");
            for (Map m:list){
                System.out.println(m);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testGoodsDTO()throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            List<GoodsDTO> list = session.selectList("selectGoodsDTO");
            for (GoodsDTO g:list){
                System.out.println(g.getGoods().getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testUpdate(){
        SqlSession sqlSession = null;
        try {
            sqlSession = myBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 739);
            goods.setTitle("hello world");
            int num = sqlSession.update("goods.update", goods);
            sqlSession.commit();
        } catch (Exception e) {
            if (sqlSession!=null){
                sqlSession.rollback();
            }
            throw e;
        } finally {
            myBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testDelete(){
        SqlSession sqlSession = null;
        try {
            sqlSession = myBatisUtils.openSession();
            sqlSession.delete("goods.delete", 739);
            sqlSession.commit();
        } catch (Exception e) {
            if (sqlSession!=null){
                sqlSession.rollback();
            }
            throw e;
        } finally {
            myBatisUtils.closeSession(sqlSession);
        }
    }

    /**
     * 预防SQL注入
     * @throws Exception
     */
    @Test
    public void testSelectByTitle() throws Exception {
        SqlSession session = null;
        try{
            session = myBatisUtils.openSession();
            Map param = new HashMap();
            /*
                ${}原文传值
                select * from t_goods
                where title = '' or 1 =1 or title = '【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'
            */
            /*
               #{}预编译
               select * from t_goods
                where title = "'' or 1 =1 or title = '【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'"
            */

            param.put("title","'' or 1=1 or title='【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'");
            param.put("order" , " order by title desc");
            List<Goods> list = session.selectList("goods.selectByTitle", param);
            for(Goods g:list){
                System.out.println(g.getTitle() + ":" + g.getCurrentPrice());
            }
        }catch (Exception e){
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }


    @Test
    public void testDynamicSQL() throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            Map map = new HashMap();
            map.put("categoryId", 44);
            map.put("currentPrice", 500);
            List<Goods> list = session.selectList("dynamicSQL", map);
            for (Goods g:list){
                System.out.println(g.getTitle() + ";" + g.getCategoryId() +";"+ g.getCurrentPrice());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            myBatisUtils.closeSession(session);
        }
    }

    /**
     * 测试一级缓存
     * @throws Exception
     */
    @Test
    public void testLv1Cache() throws Exception {
        SqlSession session = null;
        try{
            session = myBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById" , 1603);
            Goods goods1 = session.selectOne("goods.selectById" , 1603);
            System.out.println(goods.hashCode() + ":" + goods1.hashCode());
        }catch (Exception e){
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }

        try{
            session = myBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById" , 1603);
            session.commit();//commit提交时对该namespace缓存强制清空
            Goods goods1 = session.selectOne("goods.selectById" , 1603);
            System.out.println(goods.hashCode() + ":" + goods1.hashCode());
        }catch (Exception e){
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    /**
     * 测试二级缓存
     * @throws Exception
     */
    @Test
    public void testLv2Cache() throws Exception {
        SqlSession session = null;
        try{
            session = myBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById" , 1603);
            System.out.println(goods.hashCode());
        }catch (Exception e){
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }

        try{
            session = myBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById" , 1603);
            System.out.println(goods.hashCode());
        }catch (Exception e){
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testStudentSelectAll() throws Exception{
        SqlSession sqlSession = null;
        try {
            sqlSession = myBatisUtils.openSession();
            List<Student> list = sqlSession.selectList("student.selectAll");
            for(Student s: list){
                System.out.println(s.getId() + " " + s.getReg_no() + " " + s.getName() + " " + s.getSex() + " " + s.getAge() + " " + s.getGrade() + " " + s.getMajor());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            myBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void insertStudent(){
        SqlSession sqlSession = null;
        try {
            sqlSession = myBatisUtils.openSession();
            Student student = new Student();
            student.setReg_no(20171208);
            student.setName("言豫津");
            student.setSex("男");
            student.setAge(26);
            student.setGrade("2013");
            student.setMajor("哲学系");

            int n = sqlSession.insert("student.insert", student);
            sqlSession.commit();

            System.out.println(student.getId());
        } catch (Exception e) {
            if (sqlSession!=null){
                sqlSession.rollback();
            }
            throw e;
        } finally {
            myBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testDynamicStuSQL() throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            Map map = new HashMap();
            map.put("age", 30);
            map.put("sex", "男");
            List<Student> list = session.selectList("student.dynamicSQL", map);
            for (Student s:list){
                System.out.println(s.getName() + ";" + s.getAge() +";"+ s.getSex());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testOneToMany() throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            List<Goods> list = session.selectList("goods.selectOneToMany");
            for (Goods g: list){
                System.out.println(g.getTitle() + "; " + g.getGoodsDetailList().size());
            }
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testManyToOne(){
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            List<GoodsDetail> list = session.selectList("goodsDetail.selectManyToOne");
            for (GoodsDetail g: list){
                System.out.println(g.getGdPicUrl() + "; " + g.getGoods().getTitle());
            }
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectPage() throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            PageHelper.startPage(2,20);
            Page<Goods> page = (Page)session.selectList("goods.selectPage");
            System.out.println("总页数："+page.getPages());
            System.out.println("总记录数："+page.getTotal());
            System.out.println("开始行号："+page.getStartRow());
            System.out.println("结束行号："+page.getEndRow());
            System.out.println("当前页码："+page.getPageNum());
            System.out.println("页码大小："+page.getPageSize());
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testBatchInsert() throws Exception{
        SqlSession session = null;
        try {
            long st = new Date().getTime();
            session = myBatisUtils.openSession();
            List list = new ArrayList();
            for (int i=0; i<10; i++){
                Goods goods = new Goods();
                goods.setTitle("Test good");
                goods.setSubTitle("McT");
                goods.setOriginalCost(200f);
                goods.setCurrentPrice(100f);
                goods.setDiscount(0.5f);
                goods.setIsFreeDelivery(1);
                goods.setCategoryId(43);
                list.add(goods);
            }
            session.insert("goods.batchInsert", list);
            session.commit();
            long et = new Date().getTime();
            System.out.println("operation time: " + (et-st));
        } catch (Exception e) {
            if(session!=null){
                session.rollback();
            }
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testBatchDelete() throws Exception{
        SqlSession session = null;
        try {
            session = myBatisUtils.openSession();
            List list = new ArrayList();
            list.add(1920);
            list.add(1921);
            list.add(1922);
            session.delete("goods.batchDelete", list);
            session.commit();
        } catch (Exception e) {
            throw e;
        }finally {
            myBatisUtils.closeSession(session);
        }
    }
    
}
