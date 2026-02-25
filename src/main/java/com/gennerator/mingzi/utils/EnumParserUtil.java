package com.gennerator.mingzi.utils;

import com.gennerator.mingzi.entity.EnumEntity;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnumParserUtil {

    // 数字 + 中文说明（直到下一个数字或结尾）
    private static final Pattern ITEM_PATTERN =
            Pattern.compile("(\\d+)\\s*([^\\d]+)");

    // 至少一组：数字 + 中文说明
    private static final Pattern ENUM_FORMAT_PATTERN =
            Pattern.compile("^\\s*[^\\d]+(\\s*\\d+\\s*[^\\d]+)+\\s*$");

    /**
     * 判断字符串是否符合：字段说明 + (数字 + 说明)+
     */
    public static boolean isValidEnumFormat(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return ENUM_FORMAT_PATTERN.matcher(text.trim()).matches();
    }

    public static List<EnumEntity> parseToEnumEntity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        text = text.trim();

        // 1️⃣ 提取字段中文名（第一个数字前）
        int firstDigitIndex = findFirstDigitIndex(text);
        if (firstDigitIndex == -1) {
            throw new IllegalArgumentException("字符串格式不正确：" + text);
        }
        String fieldName = text.substring(0, firstDigitIndex).trim();

        // 2️⃣ 解析枚举项
        Matcher matcher = ITEM_PATTERN.matcher(text);
        List<EnumEntity> list = new ArrayList<>();

        while (matcher.find()) {
            Integer code = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2).trim();

            EnumEntity entity = new EnumEntity();
            entity.setCode(code);
            entity.setName(name);
            // 枚举常量 = 字段名 + 枚举中文 → 拼音大写
            entity.setConstant(toConstant(fieldName, name));

            list.add(entity);
        }

        return list;
    }

    /** 找第一个数字出现的位置 */
    private static int findFirstDigitIndex(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /** 枚举常量生成规则 */
    private static String toConstant(String fieldName, String name) {
        // 示例：普通用户 → PU_TONG_YONG_HU
        String pinyin = EnumParserUtil.toPinyinUpperUnderline(name);
        return pinyin.replaceAll("__+", "_");
    }

    public static String toPinyinUpperUnderline(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            try {
                // 中文
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]")) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        sb.append(pinyinArray[0]);
                    }
                } else {
                    sb.append(c);
                }

                // 多字之间加下划线
                if (i < chinese.length() - 1) {
                    sb.append("_");
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String text = "用户类型 1 普通用户 2 管理员";

        List<EnumEntity> list = parseToEnumEntity(text);
        list.forEach(System.out::println);
    }
}

