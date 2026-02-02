package com.awsliveness.android;

import com.getcapacitor.Logger;

public class AwsLiveness {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
