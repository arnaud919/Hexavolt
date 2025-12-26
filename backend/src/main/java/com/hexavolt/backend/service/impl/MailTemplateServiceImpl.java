// src/main/java/com/hexavolt/backend/service/impl/MailTemplateServiceImpl.java
package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.service.MailTemplateService;
import org.springframework.stereotype.Service;

@Service
public class MailTemplateServiceImpl implements MailTemplateService {

    @Override
    public String activationEmail(User user, String activationLink) {
        String firstName = safe(user.getFirstName());
        String link = safe(activationLink);
        return """
            <html>
              <body style="font-family:Arial,Helvetica,sans-serif">
                <h2 style="margin-bottom:8px;">Bienvenue sur Hexavolt</h2>
                <p>Bonjour %s,</p>
                <p>Merci de vous être inscrit(e). Cliquez sur le bouton ci-dessous pour activer votre compte&nbsp;:</p>
                <p style="margin:20px 0">
                  <a href="%s" style="display:inline-block;padding:10px 16px;
                     color:#2C3E50;background:#F4C542;text-decoration:none;border-radius:6px">
                     Activer mon compte
                  </a>
                </p>
                <p style="color:#555">Ce lien expire dans 24&nbsp;heures.</p>
                <hr style="border:none;border-top:1px solid #eee;margin:24px 0"/>
                <p style="font-size:12px;color:#777">Si le bouton ne fonctionne pas, copiez ce lien dans votre navigateur :<br/>
                  <span style="word-break:break-all">%s</span>
                </p>
              </body>
            </html>
            """.formatted(firstName, link, link);
    }

    @Override
    public String resetPasswordEmail(User user, String resetLink) {
        String firstName = safe(user.getFirstName());
        String link = safe(resetLink);
        return """
            <html>
              <body style="font-family:Arial,Helvetica,sans-serif">
                <h2 style="margin-bottom:8px;">Réinitialisation de mot de passe</h2>
                <p>Bonjour %s,</p>
                <p>Vous avez demandé à réinitialiser votre mot de passe. Cliquez&nbsp;:</p>
                <p style="margin:20px 0">
                  <a href="%s" style="display:inline-block;padding:10px 16px;
                     color:#2C3E50;background:#F4C542;text-decoration:none;border-radius:6px">
                     Réinitialiser mon mot de passe
                  </a>
                </p>
                <p style="color:#555">Ce lien expire dans 2&nbsp;heures.</p>
                <hr style="border:none;border-top:1px solid #eee;margin:24px 0"/>
                <p style="font-size:12px;color:#777">Lien direct :<br/>
                  <span style="word-break:break-all">%s</span>
                </p>
              </body>
            </html>
            """.formatted(firstName, link, link);
    }

    // très simple "escape" minimaliste (à remplacer par une vraie lib si besoin)
    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
