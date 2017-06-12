package com.buildit.crawler;

import com.buildit.crawler.service.ProcessController;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        new ProcessController().process(args[0]);
    }
}
