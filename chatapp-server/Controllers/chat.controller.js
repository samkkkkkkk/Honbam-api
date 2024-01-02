const Chat = require('../Models/chat');
const chatController = {};

chatController.saveChat = async (message, user) => {
  const newMessage = new Chat({
    chat: message,
    user: {
      id: user._id, // 몽고 DB에서 부여해주는 _id
      name: user.name,
    },
  });
  await newMessage.save();
  return newMessage;
};

module.exports = chatController;
