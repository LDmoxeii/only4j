package com.only4.web.core;


import cn.hutool.core.util.StrUtil;
import com.only4.common.entity.R;

/**
 * web层通用数据处理
 *
 * @author LD_moxeii
 */
public class BaseController {

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected R<Void> toAjax(int rows) {
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected R<Void> toAjax(boolean result) {
        return result ? R.ok() : R.fail();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StrUtil.format("redirect:{}", url);
    }

}
