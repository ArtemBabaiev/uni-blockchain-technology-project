package com.ababaiev.utils;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    @SneakyThrows
    public static KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static byte[] getAsFileContent(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        StringBuilder sb = new StringBuilder();

        sb.append(PRIVATE_KEY_HEADER);
        sb.append("\n");
        sb.append(Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded()));
        sb.append("\n");
        sb.append(PRIVATE_KEY_FOOTER);

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String getHash64(String data) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    @SneakyThrows
    public static byte[] sign(String data, PrivateKey privateKey) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));

        return signature.sign();
    }

    public static byte[] sign(String data, byte[] privateKey) {
        return sign(data.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    public static boolean verify(String data, byte[] signature, byte[] publicKeyBytes) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(data.getBytes(StandardCharsets.UTF_8));

            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SneakyThrows
    public static byte[] sign(byte[] data, byte[] privateKey) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(loadPrivateKey(privateKey));
        signature.update(data);

        return signature.sign();
    }

    @SneakyThrows
    public static PrivateKey loadPrivateKey(byte[] privateKeyBytes) {
        String keyContent = new String(privateKeyBytes, StandardCharsets.UTF_8)
                .replace(PRIVATE_KEY_HEADER, "")
                .replace(PRIVATE_KEY_FOOTER, "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    @SneakyThrows
    public static String getHash64(byte[] data) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return Base64.getEncoder().encodeToString(hash);
    }
}
