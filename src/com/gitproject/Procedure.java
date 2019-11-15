package com.gitproject;

public class Procedure {


//     import java.io.IOException;
//     import java.sql.Connection;
//     import java.sql.DriverManager;
//     import java.sql.SQLException;
//     import java.text.SimpleDateFormat;
//     import java.util.ArrayList;
//     import java.util.Date;
//     import java.util.HashMap;
//     import java.util.Iterator;
//     import java.util.List;
//     import java.util.Map;
//
//     import net.sf.json.JSONObject;
//     import oracle.jdbc.OracleCallableStatement;
//     import oracle.sql.ARRAY;
//     import oracle.sql.ArrayDescriptor;
//     import oracle.sql.STRUCT;
//     import oracle.sql.StructDescriptor;
//
//     import org.apache.http.HttpResponse;
//     import org.apache.http.client.ClientProtocolException;
//     import org.apache.http.client.HttpClient;
//     import org.apache.http.client.methods.HttpGet;
//     import org.apache.http.impl.client.DefaultHttpClient;
//     import org.apache.http.util.EntityUtils;
//     import org.jxstar.dao.DaoParam;
//     import org.jxstar.dao.pool.DataSourceConfig;
//     import org.jxstar.dao.pool.DataSourceConfigManager;
//     import org.jxstar.service.BusinessObject;
//     import org.jxstar.util.JsonUtil;
//     import org.jxstar.util.factory.FactoryUtil;
//     import org.jxstar.util.key.KeyCreator;
//
//     import com.alibaba.fastjson.JSON;
//     import com.geam.util.CommonQuery;
//     import com.google.gson.JsonArray;
//     import com.google.gson.JsonElement;
//     import com.google.gson.JsonObject;
//     import com.google.gson.JsonParser;
//
//     /**
//     *	BUG2019091643396 生活电器设备联机新增API接口
//     * @author ex_chenming1
//     *
//     */
//    public class UpdateMjMesMoldTimes extends BusinessObject {
//
//        private static final long serialVersionUID = 1L;
//
//        public void update() {
//            //获取组织id
//            String sysOus = CommonQuery.getValueByVarCode("MES.EAM.DEVICE.OU");
//            String[] ous = sysOus.split(",");
//            long start = new Date().getTime();
//
//            List<String> field = new ArrayList<String>();
//            field.add("INV_ORG_ID");
//            field.add("ACTUAL_CRAFTS_ID");
//            field.add("PRELOADID");
//            field.add("DATETIME_ACQUISITION");
//            field.add("DEVICE_CODE");
//            field.add("MOULD_CODE");
//            field.add("MITEM_CODE");
//            field.add("QTY");
//
//            for (String invOrgId : ous) {
//                List<Map<String, String>> list = this.queryDevicelist(invOrgId);
//                if (!list.isEmpty()) {
//                    String mark = CommonQuery.getValueByVarCode("code.party.mark");
//                    //存到数据库中
//                    if("1".equalsIgnoreCase(mark)){
//                        this.batchInsert(list, invOrgId);
//                    }else{
//                        this.batchInsertData(list,field,invOrgId);
//                    }
//                    this.updateMjInfo(invOrgId);
//                }
//            }
//
//            long end = new Date().getTime();
//            _log.showInfo("end-start=" + (end-start)/1000);
//        }
//
//
//        /**
//         * 将数据存到数据库
//         * @param list
//         */
//        public void batchInsert(List<Map<String, String>> list,String invOrgId){
//
//            String delSql = "delete from inf_dl_eai_process_route where inv_org_id=?";
//            DaoParam delParam = _dao.createParam(delSql);
//            delParam.addStringValue(invOrgId);
//            if (!_dao.update(delParam)) {
//                _log.showInfo("数据清空失败！");
//            }
//            Long begin = new Date().getTime();
//            try {
//                // sql前缀
//                String prefix = "insert into inf_dl_eai_process_route (route_id,"
//                        + "inv_org_id,actual_crafts_id,preloadid,datetime_acquisition,device_code,"
//                        + "mould_code,mitem_code,qty) values ";
//                // 保存sql后缀
//                for (Map<String, String> map : list) {
//                    StringBuilder suffix = new StringBuilder();
//                    String id = KeyCreator.getInstance().createKey("inf_dl_eai_process_route");
//                    suffix.append("( '" + id + "','");
//                    suffix.append(map.get("INV_ORG_ID") + "','");
//                    suffix.append(map.get("ACTUAL_CRAFTS_ID") + "','");
//                    suffix.append(map.get("PRELOADID") + "','");
//                    suffix.append(map.get("DATETIME_ACQUISITION") + "','");
//                    suffix.append(map.get("DEVICE_CODE") + "','");
//                    suffix.append(map.get("MOULD_CODE") + "','");
//                    suffix.append(map.get("MITEM_CODE") + "','");
//                    suffix.append(map.get("QTY"));
//                    suffix.append("')");
//                    String sql = prefix + suffix;
//                    DaoParam param = _dao.createParam(sql);
//                    if (!_dao.update(param)) {
//                        _log.showInfo("数据插入失败");
//                    }
//                }
//
//            }catch (Exception e){
//                _log.showError(e.getMessage());
//            }
//            // 结束时间
//            Long end = new Date().getTime();
//            // 耗时
//            _log.showInfo("数据插入花费时间 : " + (end - begin) / 1000 + " s");
//        }
//        /**
//         * 更新模具台账开模次数
//         * @param list
//         */
//        public void updateMjInfo(String invOrgId) {
//            _log.showInfo("--------模具台账数据更新开始----------");
//
//            //查询中间表数据
//            String selSql = "select mould_code,count(*) cnt from ("
//                    + "SELECT t.actual_crafts_id, t.mould_code  FROM inf_dl_eai_process_route t"
//                    + " where t.mould_code is not null and t.inv_org_id=? group by t.actual_crafts_id, t.mould_code"
//                    + ")group by mould_code";
//            DaoParam selParam = _dao.createParam(selSql);
//            selParam.addStringValue(invOrgId);
//            List<Map<String, String>> list = _dao.query(selParam);
//
//            _log.showInfo("list数据size=" + list.size());
//            int countS = 0;
//            int countE = 0;
//            int countD = 0;
//            StringBuilder dmsg = new StringBuilder();
//            StringBuilder emsg = new StringBuilder();
//            for (Map<String, String> map : list) {
//                String keysql = "select mes_mold_times from mj_asset_card1 where device_code=?";
//                DaoParam param3 = _dao.createParam(keysql);
//                String deviceCode = map.get("mould_code");//模具编码
//                param3.addStringValue(deviceCode);
//                Map<String, String> kMap = _dao.queryMap(param3);
//                if(kMap.isEmpty()){
//                    emsg.append(deviceCode).append(";");
//                    countE++;
//                    continue;
//                }
//                String mesMoldTimes = kMap.get("mes_mold_times");//EAM系统当前的开模次数
//                if (mesMoldTimes == null || "".equals(mesMoldTimes)) {
//                    mesMoldTimes = "0";
//                }
//                String qty1 = map.get("cnt");//新增加的开模次数
//                if (qty1 == null || "".equals(qty1)) {
//                    qty1 = "0";
//                }
//                int qty = Integer.parseInt(qty1);//新增加的开模次数
//                int mes = Integer.parseInt(mesMoldTimes);
//                int total = qty + mes;
//                String value = String.valueOf(total);
//                String sql2 = "update mj_asset_card1 set mes_mold_times =? where device_code=?";
//                DaoParam param2 = _dao.createParam(sql2);
//                param2.addStringValue(value);
//                param2.addStringValue(deviceCode);
//                _log.showInfo("mes_mold_times=" + value + ",deviceCode=" + deviceCode);
//                if (!_dao.update(param2)) {
//                    dmsg.append(deviceCode).append(";");
//                    countD++;
//                }
//                countS++;
//            }
//            _log.showInfo("模具台账数据不存在:" + emsg.toString());
//            _log.showInfo("模具台账数据更新失败:" + dmsg.toString());
//            _log.showInfo("模具台账数据更新结束,总数据[" + list.size() + "]条，成功更新数据[" + countS + "]条，更新失败[" + countD + "]条，模具台账不存在数据[" + countE + "]条");
//        }
//        /**
//         * 调用http接口获取生活电器事业部模具开合模次数数据 list
//         * @return
//         */
//        @SuppressWarnings("unchecked")
//        public List<Map<String, String>> queryDevicelist(String invOrgId) {
//            _log.showInfo("--------list数据查询开始----------");
//            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//            String url = getValueByVarCode("MES.EAM.DEVICE.Url");
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Map<String, String> map = null;
//            try {
//                map=new HashMap<String, String>();
//                Date addDay = com.geam.base.DateUtils.addDay(new Date(), -1);
//                String startDate = sdf.format(addDay);
//                String endDate = sdf.format(new Date());
//                // 调用参数：库存组织、开始时间、结束时间
//                url += "?invOrgId=" + invOrgId + "&datetimeStart=" + startDate + "&datetimeEnd=" + endDate;
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpResponse response;
//                HttpGet httppost = new HttpGet(url);
//                long start = new Date().getTime();
//                response = httpclient.execute(httppost);
//                int code = response.getStatusLine().getStatusCode();
//                long end = new Date().getTime();
//                _log.showInfo("end-start=" + (end-start)/1000);
//                _log.showInfo("http接口调用返回编码：" + code);
//                if (code == 200) {
//                    String resp = EntityUtils.toString(response.getEntity());
//                    JSONObject o = JSONObject.fromObject(resp);
//                    String deviceDetailList = o.getString("ListData");
//                    if(!deviceDetailList.isEmpty() && !"null".equalsIgnoreCase(deviceDetailList)){
//                        list = (List<Map<String, String>>) JSON.parseArray(o.getString("ListData"), map.getClass());
//                    }
//                }
//            } catch (ClientProtocolException e) {
//                _log.showError("[ClientProtocolException] " + e.getMessage());
//            } catch (Exception e) {
//                _log.showError("[Exception] " + e.getMessage());
//            }
//            _log.showInfo("--------list数据查询结束----------");
//            return list;
//        }
//        /**
//         * 获取系统变量
//         *
//         * @param varCode
//         * @return
//         */
//        private String getValueByVarCode(String varCode) {
//            String sql = " select v.var_value  from sys_var v where v.var_code =? ";
//            DaoParam param = _dao.createParam(sql);
//            param.addStringValue(varCode);
//            Map<String, String> map = _dao.queryMap(param);
//            return map.get("var_value");
//        }
//
//        /**
//         * 将数据存到数据库
//         * @param list
//         * @param field
//         */
//        public void batchInsertData(List<Map<String, String>> list, List<String> field,String invOrgId){
//            DataSourceConfig dsc = DataSourceConfigManager.getInstance().getDataSourceConfig("default");
//            String connectionURL=dsc.getJdbcUrl();
//            String userID =dsc.getUserName();
//            String userPassword =dsc.getPassWord();
//            String driverClass =dsc.getDriverClass();
//            //组装需要的参数List<String[]> String[] length 要和数据库的自定对象PARAM_OBJECT个数一样
//
//            Connection con = null;
//            OracleCallableStatement stmt = null;
//            try {
//                Class.forName(driverClass).newInstance();
//                con = DriverManager.getConnection(connectionURL, userID,
//                        userPassword);
//
//                ARRAY aArray = getArray(con, "PARAM_OBJECT1","PARAM_ARRAY1", list,field);
//
//                _log.showInfo("开始时间："+  new Date());
//                stmt = (OracleCallableStatement) con.prepareCall("{call PRO_UPDATE_EVALUATE_SCORE(?,?)}");
//
//                stmt.setARRAY(1, aArray);
//                stmt.setFixedCHAR(2,invOrgId);
//
//                stmt.execute();
//                _log.showInfo("结束时间："+  new Date());
//            } catch (SQLException e) {
//                _log.showInfo(e.getMessage());
//            } catch (Exception e) {
//                _log.showInfo(e.getMessage());
//            } finally {
//                if (stmt != null) {
//                    try {
//                        stmt.close();
//                    } catch (SQLException e) {
//                        _log.showInfo(e.getMessage());
//                    }
//                }
//                if (con != null) {
//                    try {
//                        con.close();
//                    } catch (SQLException e) {
//                        _log.showInfo(e.getMessage());
//                    }
//                }
//            }
//        }
//        /**
//         * 将java数组转换成数据库数组
//         * @param con			原生jdbc链接，链接池的链接不行
//         * @param oracleObj		数据库自定义对象
//         * @param oraclelist	数据库自定义数组
//         * @param objlist		需要的参数
//         * @param paramNum		自定义对象的个数
//         * @return
//         * @throws Exception
//         */
//        private ARRAY getArray(Connection con, String oracleObj,
//                               String oraclelist, List<Map<String,String>> objlist,List<String> field) throws Exception {
//
//            ARRAY list = null;
//
//            if (!objlist.isEmpty()) {
//
//                StructDescriptor structdesc = StructDescriptor.createDescriptor(oracleObj,con);
//
//                STRUCT[] structs = new STRUCT[objlist.size()];
//
//                Object[] result = new Object[field.size()];
//
//                for (int i = 0; i < objlist.size(); i++) {
//                    Map<String, String> map = objlist.get(i);
//
//                    // 数组大小应和你定义的数据库对象(AOBJECT)的属性的个数
//                    for(int j=0;j<field.size();j++){
//                        result[j] = map.get(field.get(j));
//                    }
//
//                    // 将list中元素的数据传入result数组 result[1] = new Integer(..)
//                    structs[i] = new STRUCT(structdesc, con, result);
//                }
//
//                ArrayDescriptor desc = ArrayDescriptor.createDescriptor(oraclelist,con);
//
//                list = new ARRAY(desc, con, structs);
//            }
//            return list;
//        }
//    }
//
//
//
//--存储过程PRO_UPDATE_EVALUATE_SCORE
//    CREATE OR REPLACE
//    procedure PRO_UPDATE_EVALUATE_SCORE(paramList in param_array1,
//                                        invOrgId in varchar2) is
//    begin
//    delete from inf_dl_eai_process_route where inv_org_id=invOrgId;
//  for i in 1 .. paramList.count loop
//    insert into inf_dl_eai_process_route
//            (route_id,inv_org_id,actual_crafts_id,preloadid,datetime_acquisition,device_code,mould_code,mitem_code,qty,add_date)
//    values(
//            seq_route_id.nextval,paramList(i).inv_org_id,paramList(i).actual_crafts_id,paramList(i).preloadid,paramList(i).datetime_acquisition,
//    paramList(i).device_code,paramList(i).mould_code,paramList(i).mitem_code,paramList(i).qty,sysdate
//);
//    end loop;
//    commit;
//    end PRO_UPDATE_EVALUATE_SCORE;
//



}
