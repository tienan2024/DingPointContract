package com.example.dingpointcontract;

import android.app.Application;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private DatabaseHelper databaseHelper;

    @Override
    protected void attachBaseContext(Context base) {
        // 调用 LocaleHelper 类的 onAttach 方法来应用用户选择的语言
        Context context = LocaleHelper.onAttach(base);
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);

        // 异步加载模拟数据
        loadSampleData();
    }

    private void loadSampleData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // 检查数据库是否已有数据
                    if (!hasSampleData()) {
                        Log.d(TAG, "开始加载模拟数据...");
                        // 插入模拟数据
                        insertSampleTemplates();
                        insertSampleContracts();
                        insertSampleLegalArticles();
                        Log.d(TAG, "模拟数据加载完成");
                    } else {
                        Log.d(TAG, "数据库已有模拟数据，跳过加载");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "加载模拟数据失败", e);
                }
                return null;
            }
        }.execute();
    }

    private boolean hasSampleData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_CONTRACT_TEMPLATES);
        return count > 0;
    }

    private void insertSampleTemplates() {
        // 合同模板模拟数据
        databaseHelper.addContractTemplate(
                "labor_contract.docx",
                "劳动合同模板",
                "甲方：__________（用人单位）\n乙方：__________（劳动者）\n" +
                        "根据《中华人民共和国劳动法》和《中华人民共和国劳动合同法》等相关法律法规，甲乙双方经协商一致，自愿签订本合同，共同遵守本合同所列条款。\n" +
                        "一、合同期限\n" +
                        "本合同期限为____年____月____日至____年____月____日。\n" +
                        "二、工作内容\n" +
                        "乙方的工作岗位（职位）为________。\n" +
                        "三、工作时间和休息休假\n" +
                        "甲方安排乙方执行标准工时制，即每日工作8小时，每周工作40小时。\n" +
                        "四、劳动报酬\n" +
                        "1.甲方每月以货币形式支付乙方工资。\n" +
                        "2.乙方工资为税前人民币________元/月。\n" +
                        "五、社会保险和福利待遇\n" +
                        "甲方依法为乙方办理养老、医疗、失业、工伤、生育等社会保险。",
                "劳动合同");

        databaseHelper.addContractTemplate(
                "lease_agreement.docx",
                "房屋租赁合同",
                "出租方（甲方）：__________\n承租方（乙方）：__________\n\n" +
                        "甲、乙双方就房屋租赁事宜，达成如下协议：\n" +
                        "一、甲方将位于________市________区________路________号的房屋出租给乙方使用。\n" +
                        "二、租赁期限\n" +
                        "租赁期共________个月，自________年________月________日起至________年________月________日止。\n" +
                        "三、租金及支付方式\n" +
                        "1.租金：每月________元。\n" +
                        "2.支付方式：________（月付/季付/半年付/年付）。\n" +
                        "四、押金\n" +
                        "乙方应于本合同签订时向甲方支付押金________元。\n" +
                        "五、甲方的权利和义务\n" +
                        "1.有权依照本合同约定向乙方收取租金。\n" +
                        "2.负责房屋维修等事宜。\n" +
                        "六、乙方的权利和义务\n" +
                        "1.有权按照本合同约定使用房屋。\n" +
                        "2.应按时支付租金。",
                "房产租赁");

        databaseHelper.addContractTemplate(
                "sale_contract.docx",
                "买卖合同",
                "卖方（甲方）：__________\n买方（乙方）：__________\n\n" +
                        "甲、乙双方经友好协商，就产品买卖事宜达成如下协议：\n" +
                        "一、产品名称、规格、数量及单价\n" +
                        "产品名称：________\n" +
                        "规格型号：________\n" +
                        "数量：________\n" +
                        "单价：人民币________元\n" +
                        "总价：人民币________元\n" +
                        "二、交货时间和地点\n" +
                        "交货时间：________年________月________日\n" +
                        "交货地点：________\n" +
                        "三、运输方式及费用负担\n" +
                        "运输方式：________\n" +
                        "运费负担：________\n" +
                        "四、验收标准及方法\n" +
                        "验收标准：________\n" +
                        "验收方法：________\n" +
                        "五、付款方式\n" +
                        "________（全款支付/分期付款/其他方式）",
                "买卖合同");
    }

    private void insertSampleContracts() {
        // 已创建合同模拟数据
        databaseHelper.addCreatedContract(
                "zhang_labor_contract_20220301.docx",
                "张三劳动合同",
                "甲方：北京科技有限公司（用人单位）\n乙方：张三（劳动者）\n" +
                        "根据《中华人民共和国劳动法》和《中华人民共和国劳动合同法》等相关法律法规，甲乙双方经协商一致，自愿签订本合同，共同遵守本合同所列条款。\n" +
                        "一、合同期限\n" +
                        "本合同期限为2022年3月1日至2025年2月28日。\n" +
                        "二、工作内容\n" +
                        "乙方的工作岗位（职位）为软件工程师。\n" +
                        "三、工作时间和休息休假\n" +
                        "甲方安排乙方执行标准工时制，即每日工作8小时，每周工作40小时。\n" +
                        "四、劳动报酬\n" +
                        "1.甲方每月以货币形式支付乙方工资。\n" +
                        "2.乙方工资为税前人民币15000元/月。\n" +
                        "五、社会保险和福利待遇\n" +
                        "甲方依法为乙方办理养老、医疗、失业、工伤、生育等社会保险。",
                1);

        databaseHelper.addCreatedContract(
                "li_lease_agreement_20220415.docx",
                "李四房屋租赁合同",
                "出租方（甲方）：王五\n承租方（乙方）：李四\n\n" +
                        "甲、乙双方就房屋租赁事宜，达成如下协议：\n" +
                        "一、甲方将位于北京市海淀区中关村南路1号的房屋出租给乙方使用。\n" +
                        "二、租赁期限\n" +
                        "租赁期共12个月，自2022年4月15日起至2023年4月14日止。\n" +
                        "三、租金及支付方式\n" +
                        "1.租金：每月5000元。\n" +
                        "2.支付方式：季付。\n" +
                        "四、押金\n" +
                        "乙方应于本合同签订时向甲方支付押金10000元。\n" +
                        "五、甲方的权利和义务\n" +
                        "1.有权依照本合同约定向乙方收取租金。\n" +
                        "2.负责房屋维修等事宜。\n" +
                        "六、乙方的权利和义务\n" +
                        "1.有权按照本合同约定使用房屋。\n" +
                        "2.应按时支付租金。",
                2);
    }

    private void insertSampleLegalArticles() {
        // 法条模拟数据
        databaseHelper.addLegalArticle(
                "第七条",
                "《中华人民共和国劳动合同法》第七条",
                "用人单位自用工之日起即与劳动者建立劳动关系。用人单位应当建立职工名册备查。",
                "劳动法");

        databaseHelper.addLegalArticle(
                "第十条",
                "《中华人民共和国劳动合同法》第十条",
                "建立劳动关系，应当订立书面劳动合同。\n" +
                        "已建立劳动关系，未同时订立书面劳动合同的，应当自用工之日起一个月内订立书面劳动合同。\n" +
                        "用人单位与劳动者在用工前订立劳动合同的，劳动关系自用工之日起建立。",
                "劳动法");

        databaseHelper.addLegalArticle(
                "第三十八条",
                "《中华人民共和国劳动合同法》第三十八条",
                "用人单位有下列情形之一的，劳动者可以解除劳动合同：\n" +
                        "（一）未按照劳动合同约定提供劳动保护或者劳动条件的；\n" +
                        "（二）未及时足额支付劳动报酬的；\n" +
                        "（三）未依法为劳动者缴纳社会保险费的；\n" +
                        "（四）用人单位的规章制度违反法律、法规的规定，损害劳动者权益的；\n" +
                        "（五）因本法第二十六条第一款规定的情形致使劳动合同无效的；\n" +
                        "（六）法律、行政法规规定劳动者可以解除劳动合同的其他情形。\n" +
                        "用人单位以暴力、威胁或者非法限制人身自由的手段强迫劳动者劳动的，或者用人单位违章指挥、强令冒险作业危及劳动者人身安全的，劳动者可以立即解除劳动合同，不需事先告知用人单位。",
                "劳动法");

        databaseHelper.addLegalArticle(
                "第二百一十二条",
                "《中华人民共和国民法典》第二百一十二条",
                "转让财产的所有权的，依照约定或者交付标的物时转移所有权，但是法律另有规定或者当事人另有约定的除外。",
                "民法典");

        databaseHelper.addLegalArticle(
                "第二百二十二条",
                "《中华人民共和国民法典》第二百二十二条",
                "当事人互有债权债务，该债权债务种类相同的，任何一方可以将自己的债权与对方的到期债务抵销；但是，根据债权债务性质、按照当事人约定或者依照法律规定不得抵销的除外。\n" +
                        "当事人主张抵销的，应当通知对方。通知自到达对方时生效。抵销不得附条件或者附期限。",
                "民法典");
    }
}
