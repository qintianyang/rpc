package com.kk.util;

import lombok.Data;
import static com.kk.constants.RpcConstants.MAGIC_NUMBER;

/**
 * RPC协议
 */
@Data
public class RpcProtocol {
    // 魔数
    private short magicNumber = MAGIC_NUMBER;
    // 内容长度
    private int contentLength;
    // 内容
    private byte[] content;

}
