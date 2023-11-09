package com.emby.boot.telegram;

import com.alibaba.fastjson.JSONObject;
import com.emby.boot.dto.req.MusicDownReq;
import com.emby.boot.dto.resp.EmbyUserInfoResp;
import com.emby.boot.dto.resp.MusicUserInfoResp;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.EmbyUserService;
import com.emby.boot.service.MusicDownloaderService;
import com.emby.boot.service.MusicUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author laojian
 * @date 2023/8/27
 */
@Component
public  class TelegramBot extends TelegramLongPollingBot {
    private final EmbyUserService embyUserService;
    private final MusicUserService musicUserService;
    private final MusicDownloaderService musicDownloaderService;
    //填你自己的token和username
    @Value("${spring.telegrambot.config.token}")
    private String token;
    @Value("${spring.telegrambot.config.username}")
    private String username;
    @Value("${spring.telegrambot.config.groupChatId}")
    private String groupChatId;
    @Value("${spring.telegrambot.config.adminId}")
    private String adminId;
    //调用的时候初始化
    public TelegramBot(DefaultBotOptions botOptions,EmbyUserService embyUserService,MusicUserService musicUserService,MusicDownloaderService musicDownloaderService) {
        super(botOptions);
        this.embyUserService = embyUserService;
        this.musicUserService=musicUserService;
        this.musicDownloaderService=musicDownloaderService;
    }

    /**
     * 一对一的
     * onUpdateReceived(Update update): 这个方法是当Telegram服务器向bot发送一个更新时调用的。
     * Update是一个对象，包含了所有可能的信息，例如收到的消息、回调查询、新的聊天参与者等等。
     * 大多数的单个交互，如接收消息或命令，会触发这个方法。
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        // 判断是否是点击了内联菜单的按钮
        if (update.hasCallbackQuery()){
            Long chatId  = update.getCallbackQuery().getFrom().getId();

            // 获取回调数据
            String callbackData = update.getCallbackQuery().getData();
            //判断是否是删除emby的内联的按钮
            if (callbackData.startsWith("embyDelete_")){
                String[] parts = callbackData.split("_");
                String embyDeleteText = parts[1];
                if ("true".equals(embyDeleteText)){
                    //执行emby删除用户

                    try {
                        embyUserService.userDelete(String.valueOf(chatId));
                        // 处理BusinessException的代码
                        StringBuilder messageBuilder = new StringBuilder();
                        messageBuilder.append("emby删除成功");
                        sendMessage(chatId, messageBuilder);
                        return;
                    } catch (Exception e) {
                        // 处理BusinessException的代码
                        StringBuilder messageBuilder = new StringBuilder();
                        messageBuilder.append("emby删除失败：\n");
                        messageBuilder.append(e.getMessage());
                        sendMessage(chatId, messageBuilder);
                        return;                    }
                }
            }
            //判断是否是删除音乐服的内联的按钮
            if (callbackData.startsWith("musicDelete_")){
                String[] parts = callbackData.split("_");
                String embyDeleteText = parts[1];
                if ("true".equals(embyDeleteText)){
                    //执行emby删除用户

                    try {
                        musicUserService.userDelete(String.valueOf(chatId));
                        // 处理BusinessException的代码
                        StringBuilder messageBuilder = new StringBuilder();
                        messageBuilder.append("音乐服删除成功");
                        sendMessage(chatId, messageBuilder);
                        return;
                    } catch (Exception e) {
                        // 处理BusinessException的代码
                        StringBuilder messageBuilder = new StringBuilder();
                        messageBuilder.append("音乐服删除失败：\n");
                        messageBuilder.append(e.getMessage());
                        sendMessage(chatId, messageBuilder);
                        return;                    }
                }
            }
            //确认音乐下载结果
            if (callbackData.startsWith("musicOk_")){
                String[] parts  = callbackData.split("_");
                Integer num = Integer.valueOf(parts[1]);
                Integer cur = Integer.valueOf(parts[2]);
                String keywords = parts[3];

                JSONObject jsonObject = musicDownloaderService.search(keywords, cur, 5).getJSONArray("list").getJSONObject(num);
                MusicDownReq musicDownReq = new MusicDownReq();
                musicDownReq.setReadableText(jsonObject.getString("readableText"));
                musicDownReq.setSinger(jsonObject.getString("singer"));
                musicDownReq.setTime_publish(jsonObject.getString("time_publish"));
                musicDownReq.setAlbum(jsonObject.getString("album"));
                musicDownReq.setPrefix(jsonObject.getString("prefix"));
                musicDownReq.setSongmid(jsonObject.getString("songmid"));
                musicDownReq.setAlbumMid(jsonObject.getString("albumMid"));
                musicDownReq.setMid(jsonObject.getString("mid"));
                musicDownReq.setTitle(jsonObject.getString("title"));
                musicDownReq.setMusicid(jsonObject.getInteger("musicid"));
                musicDownReq.setSize(jsonObject.getString("size"));
                musicDownReq.setExtra(jsonObject.getString("extra"));
                musicDownReq.setNotice(jsonObject.getString("notice"));
                try {
                    musicDownloaderService.download(musicDownReq, String.valueOf(chatId));
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append(jsonObject.getString("readableText")+",下载成功，等待扫库");
                    //发送消息
                    sendMessage(chatId,messageBuilder);
                    return;
                } catch (Exception e) {
                    // 处理BusinessException的代码
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("音乐下载失败：\n");
                    messageBuilder.append(e.getMessage());  // 假设BusinessException有getMessage方法返回错误信息
                    sendMessage(chatId, messageBuilder);
                    return;
                }

            }
            //下一页搜索结果
            if (callbackData.startsWith("musicSearch_")){
                String[] parts  = callbackData.split("_");
                Integer cur = Integer.valueOf(parts[1]);
                String keywords =parts[2];
                JSONObject search = musicDownloaderService.search(keywords, cur, 5);
                int listSize = search.getJSONArray("list").size();
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 0; i < listSize; i++) {
                    String messageList=i+1+"."+"   "+search.getJSONArray("list").getJSONObject(i).getString("readableText");
                    messageBuilder.append(messageList).append("\n");
                }
                messageBuilder.append("\n").append("现在是第 "+cur+" 页数据");
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                //创建第一行按钮:确认搜索结果
                List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
                for (int i = 0; i < listSize; i++) {
                    InlineKeyboardButton button = InlineKeyboardButton.builder().text(String.valueOf(i+1)).callbackData("musicOk_"+i+"_"+search.getJSONObject("page").getInteger("cur")+"_"+keywords).build();
                    buttonRow1.add(button);
                }
                //创建第二行按钮:说明
                List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
                InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("↑确认搜索结果， ↓下一页数据").callbackData("dummy_data").build();
                buttonRow2.add(button2);
                //创建第三行按钮:页码
                List<InlineKeyboardButton> buttonRow3 = new ArrayList<>();
                for (int i = 0; i < search.getJSONObject("page").getInteger("size")/5; i++) {
                    if (cur==i+1){
                        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text(String.valueOf(i+1+"✅")).callbackData("musicSearch_"+(i+1)+"_"+keywords).build();
                        buttonRow3.add(button3);

                    }else {
                        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text(String.valueOf(i+1)).callbackData("musicSearch_"+(i+1)+"_"+keywords).build();
                        buttonRow3.add(button3);
                    }

                }
                // 现在，为键盘创建一个列表，并将buttonRow添加为其行
                Collections.addAll(rowList,buttonRow1,buttonRow2,buttonRow3);
                InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

                //编辑回复内联消息
                editMessageText(chatId,messageId,messageBuilder,inlineKeyboardMarkup);
                return;
            }
                return;

        }
        //电报用户id
        Long chatId  = update.getMessage().getChatId();

        //先判断是否在群组或者频道
       if (checkUserInTheGroup(chatId)){
           //组装回复消息
           StringBuilder messageBuilder = new StringBuilder();
           messageBuilder.append("你还未加入频道https://t.me/paulemby");
           //发送消息
           sendMessage(chatId,messageBuilder);
           return;
       }
       // /help命令
        if ("/help".equals(update.getMessage().getText())) {
            //组装回复消息
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("chatid: " + chatId + "\n\n"); // 添加chatid信息
            messageBuilder.append("创建emby账号：/create + 用户名\n");
            messageBuilder.append("e.g：/create helloworld\n\n");
            messageBuilder.append("重置emby密码：/reset\n");
            messageBuilder.append("删除emby账户：/delete\n\n");
            messageBuilder.append("创建音乐服账号：/musiccreate + 用户名\n");
            messageBuilder.append("e.g：/musiccreate helloworld\n\n");
            messageBuilder.append("重置音乐服密码：/musicreset\n");
            messageBuilder.append("删除音乐服账户: /musicdelete\n\n");
            messageBuilder.append("查看emby线路：/embyurl\n");
            messageBuilder.append("查看音乐服线路：/musicurl\n");
            messageBuilder.append("求片：/forum 片名 TMDB链接求片\n");
            messageBuilder.append("e.g：/forum 你的名字 https://www.themoviedb.org/movie/372058\n\n");
            messageBuilder.append("反馈：/bug\n");
            messageBuilder.append("e.g：/bug 你的名字缺少字幕\n\n");
            messageBuilder.append("音乐服添加音乐：/musicsearch  关键字\n");
            messageBuilder.append("e.g：/musicsearch 你好\n\n");
            messageBuilder.append("查询账号信息：/info\n");

            //发送消息
            sendMessage(chatId, messageBuilder);
            return;
        }
        // 创建emby账号：/create + 用户名
        if (update.getMessage().getText().startsWith("/create ")){
            //创建emby账户
            String username = update.getMessage().getText().replace("/create ", "").trim();
            //先判断用户名是否符合规则
            if (!username.matches("^[a-zA-Z0-9]+$")){
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("emby注册的用户只能是数字或者英文，或者两个组合");
                //发送消息
                sendMessage(chatId,messageBuilder);
                return;
            }

            try {
                embyUserService.userNew(username,String.valueOf(chatId));
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("emby注册成功,初始密码为空，登录不需要输入，建议修改密码\n");
                messageBuilder.append("Emby用户名："+username);
                //发送消息
                sendMessage(chatId,messageBuilder);
                return;
            } catch (Exception e) {
                // 处理BusinessException的代码
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("emby注册失败：\n");
                messageBuilder.append(e.getMessage());  // 假设BusinessException有getMessage方法返回错误信息
                sendMessage(chatId, messageBuilder);
                return;
            }
        }
        //删除emby账户：/delete
        if ("/delete".equals(update.getMessage().getText())){
            //创建删除emby确定按钮
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            // 现在，为键盘创建一个列表，并将buttonRow添加为其行，第四个按钮
            List<InlineKeyboardButton> deleteButtonRow = new ArrayList<>();
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("请确认是否删除emby用户");
            InlineKeyboardButton buttonA = InlineKeyboardButton.builder().text("是").callbackData("embyDelete_true").build();
            InlineKeyboardButton buttonB = InlineKeyboardButton.builder().text("否").callbackData("embyDelete_false").build();
            deleteButtonRow.add(buttonA);
            deleteButtonRow.add(buttonB);
            Collections.addAll(rowList,deleteButtonRow);
            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
            sendMessagesendMessage(chatId,messageBuilder,inlineKeyboardMarkup);
            return;
        }
        //重置emby密码：/reset
        if ("/reset".equals(update.getMessage().getText())){
            try {
                embyUserService.userPassword(String.valueOf(chatId));
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("emby重置成功,初始密码为空，登录不需要输入，建议修改密码");
                sendMessage(chatId, messageBuilder);
                return;
            } catch (Exception e) {
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("emby重置失败");
                messageBuilder.append(e.getMessage());
                sendMessage(chatId, messageBuilder);
                return;
            }
        }
        //查看emby线路：/embyurl
        if ("/embyurl".equals(update.getMessage().getText())){
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("emby服地址：\n");
            messageBuilder.append("http://cf.imetyou.top （cf线路,可以加https访问）\n");
            messageBuilder.append("http://emby.imetyou.top:8096 （随机线路）\n");
            messageBuilder.append("http://fk.imetyou.top:8096 （落地机线路）\n");
            messageBuilder.append("http://kr.imetyou.top:22333 （首尔中转）\n");
            messageBuilder.append("http://sg.imetyou.top:22333 (新加坡中转）\n");
            messageBuilder.append("http://chun.imetyou.top:22333 (春川中转）\n");
            sendMessage(chatId, messageBuilder);
            return;
        }
        //创建音乐服账号：/musiccreate + 用户名
        if (update.getMessage().getText().startsWith("/musiccreate ")){
            //创建emby账户
            String username = update.getMessage().getText().replace("/musiccreate ", "").trim();
            //先判断用户名是否符合规则
            if (!username.matches("^[a-zA-Z0-9]+$")){
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("音乐服的注册的用户只能是数字或者英文，或者两个组合");
                //发送消息
                sendMessage(chatId,messageBuilder);
                return;
            }

            try {
                musicUserService.userNew(username,String.valueOf(chatId));
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("音乐服成功,初始密码为用户名，建议修改密码\n");
                messageBuilder.append("音乐服用户名："+username);
                //发送消息
                sendMessage(chatId,messageBuilder);
                return;
            } catch (Exception e) {
                // 处理BusinessException的代码
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("音乐服注册失败：\n");
                messageBuilder.append(e.getMessage());  // 假设BusinessException有getMessage方法返回错误信息
                sendMessage(chatId, messageBuilder);
                return;
            }
        }
        //删除音乐服账户: /musicdelete
        if ("/musicdelete".equals(update.getMessage().getText())){
            //创建删除音乐服确定按钮
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            // 现在，为键盘创建一个列表，并将buttonRow添加为其行
            List<InlineKeyboardButton> deleteButtonRow = new ArrayList<>();
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("请确认是否删除音乐服用户");
            InlineKeyboardButton buttonA = InlineKeyboardButton.builder().text("是").callbackData("musicDelete_true").build();
            InlineKeyboardButton buttonB = InlineKeyboardButton.builder().text("否").callbackData("musicDelete_false").build();
            deleteButtonRow.add(buttonA);
            deleteButtonRow.add(buttonB);
            Collections.addAll(rowList,deleteButtonRow);
            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
            sendMessagesendMessage(chatId,messageBuilder,inlineKeyboardMarkup);
            return;
        }
        //重置音乐服密码：/musicreset
        if ("/musicreset".equals(update.getMessage().getText())){
            try {
                musicUserService.userPassword(String.valueOf(chatId));
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("音乐服重置成功,初始密码为用户名，建议修改密码");
                sendMessage(chatId, messageBuilder);
                return;
            } catch (Exception e) {
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("音乐服重置失败");
                messageBuilder.append(e.getMessage());
                sendMessage(chatId, messageBuilder);
                return;
            }
        }
        //查看音乐服线路：/musicurl
        if ("/musicurl".equals(update.getMessage().getText())){
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("音乐服地址：\n");
            messageBuilder.append("chunmusic.imetyou.top  (落地服务器线路) \n");
            messageBuilder.append("cfmusic.imetyou.top (cf线路,可以加https访问)）\n");
            messageBuilder.append("music.imetyou.top  (国内网盘线路)\n");
            sendMessage(chatId, messageBuilder);
            return;
        }
        //求片：/forum 片名 TMDB链接求片
        if (update.getMessage().getText().startsWith("/forum ")){
            String regex =  "^/forum (.*?) https://www\\.themoviedb\\.org/.+";
            String  messageText= update.getMessage().getText();
            //先判断求片是否符合规则
            if (!messageText.matches(regex)){
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("求片格式不正确,请检查格式，然后重新求");
                //发送消息
                sendMessage(chatId,messageBuilder);
                return;
            }
            String[] upText = messageText.split(" ");
            //发送给管理员求片信息
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("#求片 \n");
            messageBuilder.append("影片名:"+upText[1]+"\n");
            messageBuilder.append("TMDB链接： \n");
            messageBuilder.append(upText[2]+"\n");
            messageBuilder.append("TGID：@"+update.getMessage().getFrom().getUserName());
            //发送消息
            sendMessage(Long.parseLong(adminId),messageBuilder);
            //发送给用户反馈
            StringBuilder usermMessageBuilder = new StringBuilder();
            usermMessageBuilder.append("求片已经提交给管理员");
            sendMessage(chatId,usermMessageBuilder);

            return;
        }
        //反馈：/bug
        if (update.getMessage().getText().startsWith("/bug ")){
            String  messageText= update.getMessage().getText();
            String[] upText = messageText.split(" ");
            //发送给管理员反馈信息
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("#反馈 \n");
            messageBuilder.append(upText[1]+"\n");
            messageBuilder.append("TGID：@"+update.getMessage().getFrom().getUserName());
            //发送消息
            sendMessage(Long.parseLong(adminId),messageBuilder);
            //发送给用户反馈
            StringBuilder usermMessageBuilder = new StringBuilder();
            usermMessageBuilder.append("反馈已经提交给管理员,感谢反馈!");
            sendMessage(chatId,usermMessageBuilder);

            return;
        }
        //音乐下载搜索
        if (update.getMessage().getText().startsWith("/musicsearch ")){
            //创建搜索结果显示内联按钮
            String keywords  = update.getMessage().getText().replace("/musicsearch ", "").trim();
            JSONObject search = musicDownloaderService.search(keywords,1,5);


            int listSize = search.getJSONArray("list").size();
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 0; i < listSize; i++) {
                String messageList=i+1+"."+"   "+search.getJSONArray("list").getJSONObject(i).getString("readableText");
                messageBuilder.append(messageList).append("\n");
            }
            messageBuilder.append("\n").append("现在是第 1 页数据");
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            Integer cur = search.getJSONObject("page").getInteger("cur");
            //创建第一行按钮:确认搜索结果
            List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
            for (int i = 0; i < listSize; i++) {
                InlineKeyboardButton button = InlineKeyboardButton.builder().text(String.valueOf(i+1)).callbackData("musicOk_"+i+"_"+search.getJSONObject("page").getInteger("cur")+"_"+keywords).build();
                buttonRow1.add(button);
            }
            //创建第二行按钮:说明
            List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
            InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("↑确认搜索结果， ↓下一页数据").callbackData("dummy_data").build();
            buttonRow2.add(button2);
            //创建第三行按钮:页码
            List<InlineKeyboardButton> buttonRow3 = new ArrayList<>();
            for (int i = 0; i < search.getJSONObject("page").getInteger("size")/5; i++) {
                InlineKeyboardButton button3 = InlineKeyboardButton.builder().text(String.valueOf(i+1)).callbackData("musicSearch_"+(i+1)+"_"+keywords).build();
                buttonRow3.add(button3);
            }
            // 现在，为键盘创建一个列表，并将buttonRow添加为其行
            Collections.addAll(rowList,buttonRow1,buttonRow2,buttonRow3);
            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
            //回复内联消息
            sendMessagesendMessage(chatId,messageBuilder,inlineKeyboardMarkup);

        }
        //查询账号信息：/info
        if ("/info".equals(update.getMessage().getText())){
            try {
                RestResp<EmbyUserInfoResp> userEmbyInfo = embyUserService.userInfo(String.valueOf(chatId));
                StringBuilder messageBuilder = new StringBuilder();
                if (userEmbyInfo.getData().getEmbyName()!=null){
                    messageBuilder.append("emby用户名："+userEmbyInfo.getData().getEmbyName()+"\n");
                }
                RestResp<MusicUserInfoResp> musicUserInfo = musicUserService.userInfo(String.valueOf(chatId));
                if (musicUserInfo!=null){
                    messageBuilder.append("音乐服用户名："+musicUserInfo.getData().getNavidromName()+"\n");
                }

                sendMessage(chatId, messageBuilder);
                return;
            } catch (Exception e) {
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("信息查询失败");
                messageBuilder.append(e.getMessage());
                sendMessage(chatId, messageBuilder);
                return;
            }
        }

        return;
    }

    /**
     * 一对多的
     * onUpdatesReceived(List<Update> updates): 这个方法类似于onUpdateReceived，但是它处理的是一个Update对象的列表。
     * 在某些情况下，Telegram服务器可能会在一个单一请求中发送多个更新，这个方法是为了处理这种情况的。
     * @param updates
     */
    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);

    }

    /**
     * getBotUsername(): 这个方法返回bot的用户名。
     * @return
     */
    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    /**
     * 注册机器人
     * onRegister(): 当bot注册到TelegramBotsApi时，此方法会被调用。
     */
    @Override
    public void onRegister() {
        super.onRegister();
    }

    /**
     * 判断用户是否在频道的方法
     * @param chatId
     * @return true是还未加入，false是已经加入
     */
    public boolean checkUserInTheGroup(long chatId){
        //检测用户在对应频道的信息
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(groupChatId);
        getChatMember.setUserId(chatId);

        try {
            ChatMember chatMember  = execute(getChatMember);
            //在频道的身份
            String status = chatMember.getStatus();
            //在频道是这些身份说明用户在频道
            if ("administrator".equals(status)||"creator".equals(status)||"member".equals(status)){
                return false;
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 发送消息的方法
     */
    public void sendMessage(long chatId,StringBuilder messageBuilder){
        SendMessage message = SendMessage.builder()
                .text(messageBuilder.toString())
                .chatId(chatId)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内联回复的方法
     */
    public void  sendMessagesendMessage(long chatId, StringBuilder messageBuilder,InlineKeyboardMarkup inlineKeyboardMarkup){
        SendMessage message = SendMessage.builder()
                .text(messageBuilder.toString())
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑回复消息
     */
    public void editMessageText(long chatId, Integer messageId,StringBuilder messageBuilder,InlineKeyboardMarkup inlineKeyboardMarkup){
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId); // 你需要知道要编辑消息的 ID
        editMessage.setText(messageBuilder.toString());
        editMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(editMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
