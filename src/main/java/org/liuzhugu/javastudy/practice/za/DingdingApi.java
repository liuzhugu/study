package org.liuzhugu.javastudy.practice.za;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import org.springframework.http.HttpMethod;

public class DingdingApi {
    private static final String accessToken = "b6264e26783d3d04a3b96f254e5d50c9";

    public static void main(String[] args) {
        //getUatToken();
        //getPrdToken();
        //getRange();
        //getUserInfo("5157628536899063371");
        //getUserList(489474151);
        //getSubDept(489812097);
        //549063289,503043294,529699172,489397100,489801097,489283111
        getDept(489801097);
        //getDeptUser(489474151);
        //getSubDeptList(1L);
        //getUserInfoByTmpCode("d6dcaba5a31f3103ba9148cf86f0579a");
    }


    public static void getUatToken() {
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

    public static void getPrdToken() {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey("dingtbb4qjb4q1j7zbyz");
            req.setAppsecret("R3bayh4sPM51SAQrvQfdyLvV3lzgANdHb-6KiRBxZ6XxJ6w_zMX7GfOnty2hgo34");
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

    public static void getUserInfoByDingCode(String tmpAuthCode) {
       try {
           DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
           OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
           request.setCode(tmpAuthCode);
           request.setHttpMethod(HttpMethod.GET.name());
           OapiUserGetuserinfoResponse response = null;
           response = client.execute(request,accessToken);
           System.out.println(response.getBody());
       } catch (ApiException e) {
           e.printStackTrace();

       }
    }

    public static void getUserInfoByTmpCode(String tmpAuthCode) {
        try {
            DefaultDingTalkClient  client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
            OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
            req.setTmpAuthCode(tmpAuthCode);
            OapiSnsGetuserinfoBycodeResponse response = client.execute(req,accessToken);
            System.out.println(response.getBody());
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

    public static void getSubDept(long deptId) {
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

    public static void getDept(long deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
            OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
            req.setDeptId(deptId);
            OapiV2DepartmentGetResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static void getDeptUser(long deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
            OapiV2UserListRequest req = new OapiV2UserListRequest();
            req.setDeptId(deptId);
            req.setCursor(0L);
            req.setSize(100L);
            OapiV2UserListResponse rsp = client.execute(req, accessToken);
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

    public static void getSubDeptList(long deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsubid");
            OapiV2DepartmentListsubidRequest req = new OapiV2DepartmentListsubidRequest();
            req.setDeptId(deptId);
            OapiV2DepartmentListsubidResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
