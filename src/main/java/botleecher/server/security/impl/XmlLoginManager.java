package botleecher.server.security.impl;

import botleecher.server.security.LoginManager;
import botleecher.server.security.annotations.DefaultLogin;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class XmlLoginManager implements LoginManager {

    public static final String LOGIN_FILE = System.getProperty("user.home") + File.separator + "botleecher_logins.tmp";

    @Override
    @DefaultLogin
    public boolean isLoginValid(String login, String password) throws Exception {
        final File file = new File(LOGIN_FILE);
        boolean isValid = false;
        if (StringUtils.isNotBlank(login) && StringUtils.isNotBlank(password) && file.exists()) {
            final Document document = new SAXBuilder().build(file);
            final Element root = document.getRootElement();
            if (root != null) {
                for (Element user : root.getChildren("user")) {
                    final String value = user.getAttributeValue("login");
                    if (StringUtils.equalsIgnoreCase(login, value)) {
                        final String pass = user.getAttributeValue("pass");
                        isValid = BCrypt.checkpw(password, pass);
                        break;
                    }
                }
            }
        }
        return isValid;
    }

    @Override
    public void addLogin(String login, String password) throws JDOMException, IOException {
        if (StringUtils.isBlank(login) || StringUtils.isBlank(password)) {
            return;
        }
        synchronized (this) {
            final File file = new File(LOGIN_FILE);
            final Document document;
            if (!file.exists()) {
                document = new Document();
                document.setRootElement(new Element("users"));
            } else {
                document = new SAXBuilder().build(file);
                for (Element user : document.getRootElement().getChildren("user")) {
                    if (StringUtils.equalsIgnoreCase(user.getAttributeValue("login"), login)) {
                        return;
                    }
                }

            }

            final Element element = new Element("user");
            element.setAttribute("login", login);
            element.setAttribute("pass", BCrypt.hashpw(password, BCrypt.gensalt()));
            document.getRootElement().addContent(element);
            new XMLOutputter().output(document, new FileOutputStream(file));
        }
    }

    @Override
    public void deleteLogin(final String login) throws JDOMException, IOException {
        if (StringUtils.isBlank(login)) {
            return;
        }
        synchronized (this) {
            final File file = new File(LOGIN_FILE);
            if (file.exists()) {
                final Document document = new SAXBuilder().build(file);
                final Object object = XPathFactory.instance().compile("/users/user[@login='" + login + "']").evaluateFirst(document);
                if (object instanceof Element) {
                    ((Element) object).detach();
                    new XMLOutputter().output(document, new FileOutputStream(file));
                }
            }
        }
    }

    @Override
    public int countUsers() throws Exception {
        final File file = new File(LOGIN_FILE);
        int count = 0;
        if (file.exists()) {
            final Document document = new SAXBuilder().build(file);
            if (document != null && document.getRootElement() != null) {
                final List<Element> users = document.getRootElement().getChildren("user");
                if (users != null) {
                    count = users.size();
                }
            }
        }
        return count;
    }

    @Override
    public List<String> getAllUsers() throws JDOMException, IOException {
        final File file = new File(LOGIN_FILE);
        final List<String> users = new ArrayList<String>();
        if (file.exists()) {
            final Document document = new SAXBuilder().build(file);
            final Element root = document.getRootElement();
            if (root != null) {
                for (Element user : root.getChildren("user")) {
                    final String login = user.getAttributeValue("login");
                    if (StringUtils.isNotBlank(login)) {
                        users.add(login);
                    }
                }
            }
        }
        return users;
    }
}
