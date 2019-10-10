package com.sly.seckill.exception;

import com.sly.seckill.result.CodeMsg;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/24 18:51
 * @package com.sly.seckill.exception
 **/
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private CodeMsg cm;
    public GlobalException(CodeMsg cm){
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }

    public void setCm(CodeMsg cm) {
        this.cm = cm;
    }
}
