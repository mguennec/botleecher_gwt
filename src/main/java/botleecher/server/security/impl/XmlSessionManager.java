package botleecher.server.security.impl;

import botleecher.server.security.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class XmlSessionManager implements SessionManager {

    public static final String SESSION_FILE = System.getProperty("user.home") + File.separator + "botleecher_sessions.tmp";

    @Override
    public String createSession(final String user, final String ip) throws JDOMException, IOException {
        final String uuid = UUID.randomUUID().toString();

        startSession(user, uuid, ip);
        return uuid;
    }

    @Override
    public void deleteSession(String sessionId) throws JDOMException, IOException {
        synchronized (this) {
            final File file = new File(SESSION_FILE);
            if (file.exists()) {
                final Document document = new SAXBuilder().build(file);
                final Object object = XPathFactory.instance().compile("/sessions/session[@uuid='" + sessionId + "']").evaluateFirst(document);
                if (object instanceof Element) {
                    ((Element) object).detach();
                    new XMLOutputter().output(document, new FileOutputStream(file));
                }
            }
        }
    }

    @Override
    public void deleteSessionsByUser(String login) throws JDOMException, IOException {
        synchronized (this) {
            final File file = new File(SESSION_FILE);
            if (file.exists()) {
                final Document document = new SAXBuilder().build(file);
                final List<Object> objects = XPathFactory.instance().compile("/sessions/session[@user='" + login + "']").evaluate(document);
                if (objects != null) {
                    for (Object object : objects) {
                        if (object instanceof Element) {
                            ((Element) object).detach();
                        }
                    }
                    if (objects.size() > 0) {
                        new XMLOutputter().output(document, new FileOutputStream(file));
                    }
                }
            }
        }
    }

    @Override
    public boolean checkSession(final String user, final String uuid, final String ip) throws JDOMException, IOException {
        final Date now = new Date();
        final File file = new File(SESSION_FILE);
        boolean isValid = false;
        if (file.exists()) {
            final Document document = new SAXBuilder().build(file);
            final Element root = document.getRootElement();
            if (root != null) {
                final Object object = XPathFactory.instance().compile("/sessions/session[@uuid='" + uuid + "']").evaluateFirst(document);
                if (object instanceof Element) {
                    Element session = (Element) object;
                    if (StringUtils.equalsIgnoreCase(user, session.getAttributeValue("user")) && StringUtils.equalsIgnoreCase(ip, session.getAttributeValue("ip"))) {
                        final String end = session.getAttributeValue("end");
                        final String start = session.getAttributeValue("start");
                        if (end != null && NumberUtils.isDigits(end) && start != null && NumberUtils.isDigits(start)) {
                            if (Long.valueOf(end) > now.getTime() && Long.valueOf(start) < now.getTime()) {
                                isValid = true;
                            }
                        }
                    }
                }
            }
        }
        return isValid;
    }

    private void startSession(final String user, final String uuid, final String ip) throws JDOMException, IOException {
        final Date now = new Date();
        final Date end = new Date(now.getTime() + SESSION_DURATION);
        saveSession(new Session(user, uuid, ip, now, end));
    }

    private void saveSession(Session session) throws JDOMException, IOException {
        synchronized (this) {
            final File file = new File(SESSION_FILE);
            final Document document;
            if (!file.exists()) {
                document = new Document();
                document.setRootElement(new Element("sessions"));
            } else {
                document = new SAXBuilder().build(file);
            }
            checkExpiredSession(document);
            addSession(document, session);
            new XMLOutputter().output(document, new FileOutputStream(file));
        }
    }

    private void addSession(Document document, Session session) {
        if (document != null && document.getRootElement() != null) {
            final Element element = new Element("session");
            element.setAttribute("user", session.getUser());
            element.setAttribute("uuid", session.getUuid());
            element.setAttribute("ip", session.getIp());
            element.setAttribute("start", String.valueOf(session.getStart().getTime()));
            element.setAttribute("end", String.valueOf(session.getEnd().getTime()));
            document.getRootElement().addContent(element);
        }
    }

    private void checkExpiredSession(final Document document) {
        final Date now = new Date();
        synchronized (this) {
            if (document != null) {
                for (final Object session : XPathFactory.instance().compile("/sessions/session[@end<" + now.getTime() + "]").evaluate(document)) {
                    if (session instanceof Element) {
                        ((Element) session).detach();
                    }
                }
            }
        }

    }

}
