package com_7idear.framework.ext;

import android.text.method.ReplacementTransformationMethod;

/**
 * 输入变小写
 * @author ieclipse 19-12-10
 * @description 使用方法：Edit.setTransformationMethod(new InputToLower())
 */
public class InputToLower
        extends ReplacementTransformationMethod {

    @Override
    protected char[] getOriginal() {
        char[] upper = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return upper;

    }

    @Override
    protected char[] getReplacement() {
        char[] lower = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return lower;
    }

}
