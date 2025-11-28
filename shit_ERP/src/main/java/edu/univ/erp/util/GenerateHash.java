package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        String plain = "stu3";  // <<< write the password you want
        String hash = BCrypt.hashpw(plain, BCrypt.gensalt(10));
        System.out.println("Plain: " + plain);
        System.out.println("Hash:  " + hash);
    }
}
