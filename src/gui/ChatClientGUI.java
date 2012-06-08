package gui;


public class ChatClientGUI extends java.awt.Frame {

    /**
     * Creates new form Gui_Chat_Client
     */
    public ChatClientGUI(ChatClientGUIInterface guiInterface) {
        initComponents();
        this.guiInterface = guiInterface;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lstAllReachables = new java.awt.List();
        panel1 = new java.awt.Panel();
        tbChat = new java.awt.TextArea();
        tbMessage = new java.awt.TextArea();
        btnSend = new java.awt.Button();
        txtDialogPartner = new java.awt.Label();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        lstAllReachables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstAllReachablesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(lstAllReachables, gridBagConstraints);

        panel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        panel1.add(tbChat, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        panel1.add(tbMessage, gridBagConstraints);

        btnSend.setActionCommand("btnSendActionCommand");
        btnSend.setLabel("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        panel1.add(btnSend, gridBagConstraints);

        txtDialogPartner.setText("Dialog Partner");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panel1.add(txtDialogPartner, gridBagConstraints);

        add(panel1, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {                          
        System.exit(0);
    }                         

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {                                        
    	guiInterface.sendMessage(lstAllReachables.getSelectedItem(), tbMessage.getText());
    }                                       

    private void lstAllReachablesActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    // Variables declaration - do not modify
    private ChatClientGUIInterface guiInterface;
    private java.awt.Button btnSend;
    java.awt.List lstAllReachables;
    private java.awt.Panel panel1;
    java.awt.TextArea tbChat;
    java.awt.TextArea tbMessage;
    private java.awt.Label txtDialogPartner;
    // End of variables declaration
}
