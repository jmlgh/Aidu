package jjv.uem.com.aidu.Model;

public class MensajeChat {
    private String msgTexto;
    private String msgEmisor;
    private String msgHora;
    private String msgEmail;

    public MensajeChat() {}

    public MensajeChat(String msg, String e, String c, String msgHora) {
        this.msgTexto = msg;
        this.msgEmisor = e;
        this.msgEmail = c;

        // consigue la hora actual
        this.msgHora = msgHora;
    }

    public String getMsgTexto() {
        return msgTexto;
    }

    public void setMsgTexto(String msgTexto) {
        this.msgTexto = msgTexto;
    }

    public String getMsgEmisor() {
        return msgEmisor;
    }

    public void setMsgEmisor(String msgEmisor) {
        this.msgEmisor = msgEmisor;
    }

    public String getMsgHora() {
        return msgHora;
    }

    public void setMsgHora(String msgHora) {
        this.msgHora = msgHora;
    }

    public String getMsgEmail() {
        return msgEmail;
    }

    public void setMsgEmail(String msgEmail) {
        this.msgEmail = msgEmail;
    }
}

