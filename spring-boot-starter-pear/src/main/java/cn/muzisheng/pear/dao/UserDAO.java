package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.ScaleException;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.properties.UserProperties;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Component
public class UserDAO {

    private final UserMapper userMapper;

    private final UserProperties userProperties;

    @Autowired
    public UserDAO(UserMapper userMapper, UserProperties userProperties){
        this.userMapper = userMapper;
        this.userProperties = userProperties;
    }

    /**
     * 更新用户数据
     **/
    public boolean updateUserFields(User user, Map<String, String> fields){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 遍历map，设置要更新的字段
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            updateWrapper.set(fieldName, fieldValue);
        }
        // 执行更新操作
        int rowsAffected = userMapper.update(user, updateWrapper);

        return rowsAffected > 0;
    }

    /**
     * 设置加密密码
     **/

    public boolean setPassword(User user, String password) {
        String newPassword = HashPassword(password);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 设置新的密码
        updateWrapper.set("password", newPassword);

        // 执行更新操作
        int rowsAffected = userMapper.update(user, updateWrapper);

        return rowsAffected > 0;
    }
    /**
     * 根据邮箱获取用户
     **/
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据邮箱创建用户
     **/
    public User createUser(String email, String password) {
        User user = new User(email, HashPassword(password));
        user.setActivated(false);
        user.setEnabled(true);
        if(userMapper.insert(user)>0){
            return user;
        }else {
            return null;
        }
    }
    /**
     * 根据主键更新用户
     **/
    public boolean save(User user){
        int rowsAffected = userMapper.updateById(user);
        return rowsAffected > 0;
    }
    /**
     * 根据邮箱判断用户是否存在
     **/
    public boolean isExistsByEmail(String email) {
        return getUserByEmail(email)==null;
    }


    private String HashPassword(String password) {
        if("".equals(password)){
            return "";
        }
        try {
            MessageDigest md5=MessageDigest.getInstance("SHA-256");
            byte[] digest=md5.digest((userProperties.salt + password).getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new ScaleException("The password encryption algorithm is not supported",e);
        }
    }
    public static String bytesToHex(byte[] bytes) {
        try {
            return Hex.encodeHexString(bytes);
        } catch (Exception e) {
            throw new ScaleException("Bytes cannot be converted to hexadecimal strings, password encryption failure", e);
        }
    }

    public static byte[] hexToBytes(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (Exception e) {
            throw new ScaleException("Hexadecimal strings cannot be converted to bytes, password decode failure", e);
        }
    }
}
