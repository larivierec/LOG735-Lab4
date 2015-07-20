package client.ui;

import client.model.*;
import interfaces.IObserver;
import messages.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ChatPanel extends JPanel implements IObserver {

    private ClientConnection mClientConnection;

    private JLabel mRoomLabel = new JLabel("Room List");
    private DefaultListModel<String> mRoomListModel = new DefaultListModel();
    private JList<String> mRoomList = new JList<>(mRoomListModel);
    private JLabel mClientLabel = new JLabel("Client List");
    private JList<String> mClientList = new JList<>();

    private JLabel mConnectedAsLabel = new JLabel("Connected As: ");
    private JLabel mConnectedAs = new JLabel();
    private JLabel mCurrentLobbyLabel = new JLabel("Current Room: ");
    private JLabel mCurrentLobby = new JLabel();

    private DefaultListModel<String> mChatHistoryModel = new DefaultListModel();
    private JList<String> mChatHistory = new JList<>(mChatHistoryModel);

    private JTextArea mTextArea = new JTextArea(3, 2);
    private JButton mSendMessageButton = new JButton("Send");

    private JTextField mCreateRoomField = new JTextField();
    private JPasswordField mCreateRoomPassField = new JPasswordField();
    private JButton mCreateRoomButton = new JButton("Create Room");
    private String presentRoomName = "";
    JScrollPane scrollPane;

    public ChatPanel() {
        JListRenderer j = new JListRenderer();
        mChatHistory.setCellRenderer(j);

        this.setLayout(new BorderLayout());
        mRoomLabel.setBounds(new Rectangle(2, 2, 130, 30));
        mRoomList.setBounds(new Rectangle(2, 40, 130, 570));
        mClientLabel.setBounds(new Rectangle(134, 2, 130, 30));
        mClientList.setBounds(new Rectangle(134, 40, 130, 570));
        mConnectedAsLabel.setBounds(new Rectangle(290, 2, 120, 30));
        mConnectedAs.setBounds(new Rectangle(410, 2, 100, 30));
        mCurrentLobbyLabel.setBounds(new Rectangle(480, 2, 120, 30));
        mCurrentLobby.setBounds(new Rectangle(610, 2, 100, 30));
        mChatHistory.setBounds(new Rectangle(290, 35, 1000, 1000));
        mTextArea.setBounds(new Rectangle(290, 400, 250, 100));
        mSendMessageButton.setBounds(new Rectangle(550, 500, 100, 30));

        scrollPane = new JScrollPane(mChatHistory, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(new Rectangle(290, 35, 350, 350));

        mCreateRoomField.setBounds(new Rectangle(290, 580, 100, 30));
        mCreateRoomPassField.setBounds(new Rectangle(290, 620, 100, 30));
        mCreateRoomButton.setBounds(new Rectangle(390, 580, 100, 30));

        mSendMessageButton.addActionListener(e -> {
                    mClientConnection.sendMessage(mTextArea.getText());
                    mTextArea.setText("");
                }
        );

        mCreateRoomButton.addActionListener(e -> {
            mClientConnection.sendCreateRoom(mCreateRoomField.getText(), mCreateRoomPassField.getPassword());
        });

        mRoomList.addListSelectionListener(e -> {
            JList list = (JList) e.getSource();
            String roomName = mRoomListModel.getElementAt(list.getSelectedIndex());
            mClientConnection.sendSwitchRoom(roomName);
            System.out.println("listen");
        });

        mRoomList.addMouseListener(new PopClickListener());

        mRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mChatHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.add(mRoomLabel);
        this.add(mRoomList);
        this.add(mClientLabel);
        this.add(mClientList);
        this.add(mConnectedAsLabel);
        this.add(mConnectedAs);
        this.add(mCurrentLobbyLabel);
        this.add(mCurrentLobby);
        this.add(scrollPane);
        //this.add(mChatHistory);
        this.add(mTextArea);
        this.add(mSendMessageButton);
        this.add(mCreateRoomField);
        this.add(mCreateRoomPassField);
        this.add(mCreateRoomButton);
        this.add(new JLabel());
        this.setVisible(true);

    }

    public void setConnectedAs() {
        this.mConnectedAs.setText(PersistantUser.getInstance().getLoggedInUser().getUsername());
    }

    public void setCurrentLobby() {
        this.mCurrentLobby.setText(PersistantUser.getInstance().getChatRoom().getName());
    }

    @Override
    public void update(Observable e, Object t) {

        if (t instanceof Message) {

            Message localMessage = (Message) t;
            String command = (String) localMessage.getData()[0];
            System.out.println(command);

            if (command.equals("PrivateMessage")) {
                String userName = (String) localMessage.getData()[1];
            } else if (command.equals("RoomUserList")) {
                ChatRoom c = (ChatRoom) localMessage.getData()[1];
                List<ChatRoom> rooms = (List<ChatRoom>) localMessage.getData()[2];
                populateUserList(rooms);
            } else if (command.equals("RoomList")) {
                List<ChatRoom> roomList = (List<ChatRoom>) localMessage.getData()[1];
                mRoomListModel.clear();

                for (int i = 0; i < roomList.size(); i++) {
                    mRoomListModel.addElement(roomList.get(i).getName());
                    populateUserList(roomList.get(i));
                }

            } else if (command.equals("LobbyMessage")) {

                LobbyMessage theLobbyMessage = (LobbyMessage) localMessage.getData()[1];
                if (PersistantUser.getInstance().getChatRoom().getName().equals(theLobbyMessage.getLobbyName()))
                    mChatHistoryModel.addElement(theLobbyMessage.toString());
            } else if (command.equals("MessagesInRoom")) {
                ArrayList<LobbyMessage> messages = (ArrayList<LobbyMessage>) localMessage.getData()[1];
                messages.forEach(lobbyMessage -> {
                    mChatHistoryModel.addElement(lobbyMessage.toString());
                });
            } else if (command.equals("NewChatRoom")) {
                ChatRoom incomingRoom = (ChatRoom) localMessage.getData()[1];
                populateUserList(incomingRoom);

                PersistantUser.getInstance().setChatRoom(incomingRoom);
                this.mCurrentLobby.setText(incomingRoom.getName());
                this.mChatHistoryModel.clear();
            } else if (command.equals("ChangeRoom")) {

                List<ChatRoom> list = (List<ChatRoom>) localMessage.getData()[1];
                populateUserList(list);
            } else if (command.equals("AcknowledgeRoomChange")) {

                ChatRoom room = ((ChatRoom) localMessage.getData()[1]);
                PersistantUser.getInstance().setChatRoom(room);
                this.mCurrentLobby.setText(room.getName());
                populateUserList(room);

                mChatHistoryModel.clear();

                for (LobbyMessage message : room.getRoomHistory()) {
                    this.mChatHistoryModel.addElement(message.toString());
                }
            }
        }
    }

    private void populateUserList(ChatRoom c) {

        if (c.getName().equals(PersistantUser.getInstance().getChatRoom().getName())) {

            String[] userArray = new String[c.getConnectedUsers().size()];

            c.getConnectedUsers().toArray(userArray);
            PersistantUser.getInstance().getChatRoom().setConnectedUsers(c);

            mClientList.setListData(userArray);
            mClientList.invalidate();
            mClientList.repaint();
        }
    }

    private void populateUserList(List<ChatRoom> list) {

        for(ChatRoom chatRoom:list) {
            populateUserList(chatRoom);
        }
    }

    public void setClientConnection(ClientConnection c, ChatRoom chatRoom) {
        this.mClientConnection = c;
        PersistantUser.getInstance().setChatRoom(chatRoom);

    }

    class JListRenderer implements ListCellRenderer {
        JLabel listElement;

        public JListRenderer() {
            listElement = new JLabel();
        }

        Color[] possibleColors = new Color[]{Color.BLUE, Color.ORANGE, Color.green, Color.magenta, Color.gray, Color.cyan, Color.pink};

        public Component getListCellRendererComponent(JList table, Object value, int row, boolean selected, boolean focus) {

            String messageToBeShown = value.toString();
            String usernameToCompare = messageToBeShown.split(" : ")[0];
            listElement.setText(messageToBeShown);

            Color color = Color.BLACK;
            listElement.setHorizontalAlignment(SwingConstants.RIGHT);

            List<String> connectedUsers = PersistantUser.getInstance().getChatRoom().getConnectedUsers();
            for (int i = 0; i < connectedUsers.size(); i++) {

                String user = connectedUsers.get(i);
                System.out.println(connectedUsers.size());
                if (!user.equals(PersistantUser.getInstance().getLoggedInUser().getUsername())) {

                    System.out.println(usernameToCompare);
                    System.out.println(user);

                    if (usernameToCompare.contains(user)) {

                        listElement.setHorizontalAlignment(SwingConstants.LEFT);
                        color = possibleColors[i % possibleColors.length];
                        continue;
                    }
                }
            }

            listElement.setForeground(color);

            mChatHistory.setVisibleRowCount(mChatHistoryModel.getSize());

            mChatHistory.revalidate();
            scrollPane.revalidate();
            return listElement;
        }
    }

    class PopUpDemo extends JPopupMenu {
        JMenuItem anItem;
        public PopUpDemo(){
            anItem = new JMenuItem("Click Me!");
            add(anItem);
        }
    }

    class PopClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e){
            if (e.isPopupTrigger())
                doPop(e);
        }

        public void mouseReleased(MouseEvent e){
            if (e.isPopupTrigger())
                doPop(e);
        }

        private void doPop(MouseEvent e){
            PopUpDemo menu = new PopUpDemo();
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }


}
