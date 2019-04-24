package com.lingkarin.dev.chatapp.data.source;

import com.lingkarin.dev.chatapp.EventService.XMPPEventListener;
import com.lingkarin.dev.chatapp.EventService.XMPPEventService;

/**
 *  Merupakan interface yang digunakan untuk menghandle
 *  kegiatan event serverXMPP->Android dan juga Android->serverXMPP.
 *  Gabungan dari interface XMPP EventListener dan XMPP EventService
 */
public interface DataSource extends XMPPEventService, XMPPEventListener {

}
