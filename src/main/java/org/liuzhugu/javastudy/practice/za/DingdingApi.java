package org.liuzhugu.javastudy.practice.za;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;

public class DingdingApi {
    private static final String accessToken = "6b5f3d631b23322bbf3fc4bb7976d271";

    public static void main(String[] args) {
        //getToken();
        //getRange();
        //getUserInfo("0106673448181291257");
        //getUserList(500157314);
        getDept(489166258);
    }

    public static void getToken() {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey("dingutzqbn8a1bjzh2pp");
            req.setAppsecret("xGOBa-3CJFiMDvj4CftlPt5N3DICw3dIBAun3Bt5QhLCQbnZrZooqp6L_8IKFUJI");
            req.setHttpMethod("GET");
            OapiGettokenResponse rsp = client.execute(req);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static void getRange() {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/auth/scopes");
            OapiAuthScopesRequest req = new OapiAuthScopesRequest();
            req.setHttpMethod("GET");
            OapiAuthScopesResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static void getUserInfo(String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userId);
            OapiV2UserGetResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static void getDept(long deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
            OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
            req.setDeptId(deptId);
            OapiV2DepartmentListsubResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static void getUserList(long deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/listid");
            OapiUserListidRequest req = new OapiUserListidRequest();
            req.setDeptId(deptId);
            OapiUserListidResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
