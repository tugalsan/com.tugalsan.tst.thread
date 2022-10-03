package com.tugalsan.tst.thread;

import com.tugalsan.api.file.client.TGS_FileUtilsTur;
import com.tugalsan.api.file.pdf.sign.server.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import java.nio.file.*;
import java.util.*;
import org.fusesource.jansi.AnsiConsole;

public class Main {

    private static TS_Log d = TS_Log.of(Main.class);

    public static void main(String[] args) {
        {
            var pdfPathOut = Path.of("D:\\xampp_data\\SSL\\sample_signed.pdf");
            TS_FileUtils.deleteFileIfExists(pdfPathOut);
            if (TS_FileUtils.isExistFile(pdfPathOut)) {
                d.ce("main", "Cannot delete fileOut", pdfPathOut);
                return;
            }
            d.cr("main", "precheck ok");
        }
        var pdfPath = Path.of("D:\\xampp_data\\SSL\\sample.pdf");
        var keyPath = Path.of("D:\\xampp_data\\SSL\\tomcat.jks");
        if (!TS_FileUtils.isExistFile(keyPath)) {
            d.ce("main", "File not exists", keyPath);
            return;
        }
        d.cr("main", "give me key pass");
        var keyPass = new Scanner(System.in).nextLine();
        d.cr("main", "signing...", keyPass);

        var cfg = new TS_FilePdfSignSslCfg(keyPath, keyPass);

        //TSA NULL
        var outFile = TS_FilePdfSignUtils.signIfNotSignedBefore(cfg, pdfPath, "_signName_", "_signLoc_", "_signReason_");
        TS_FileUtils.rename(outFile, TS_FileUtils.getNameLabel(outFile) + "_null.pdf");

        //TSA X
        for (var tsa : TS_FilePdfSignUtils.lstTsa()) {
            var tsaName = TGS_FileUtilsTur.toSafe(tsa.toString());
            cfg.setTsa(tsa);
            try {
                outFile = TS_FilePdfSignUtils.signIfNotSignedBefore(cfg, pdfPath, "_signName_", "_signLoc_", "_signReason_");
                TS_FileUtils.rename(outFile, TS_FileUtils.getNameLabel(outFile) + "_" + tsaName + ".pdf");
            } catch (Exception e) {
                System.err.println("ERROR ON TSA: " + tsaName);
                e.printStackTrace();
            }
        }

        d.cr("main", "check", outFile);
    }
}
