package org.darkstorm.darkbot.ircbot.handlers;

import java.util.*;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.events.*;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger.IRCLogType;

public class EventHandler extends IRCHandler implements MessageListener,
		NickServListener, DCCListener {
	private final List<MessageListener> messageListeners;
	private final List<NickServListener> nickServListeners;
	private final List<DCCListener> dccListeners;

	public EventHandler(IRCBot bot) {
		super(bot);
		messageListeners = new ArrayList<MessageListener>();
		nickServListeners = new ArrayList<NickServListener>();
		dccListeners = new ArrayList<DCCListener>();
	}

	@Override
	public String getName() {
		return "EventHandler";
	}

	@Override
	public void onMessageSent(MessageEvent event) {
		synchronized(messageListeners) {
			for(MessageListener listener : messageListeners) {
				try {
					listener.onMessageSent(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onRawSent(MessageEvent event) {
		synchronized(messageListeners) {
			for(MessageListener listener : messageListeners) {
				try {
					listener.onRawSent(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onMessageReceived(MessageEvent event) {
		synchronized(messageListeners) {
			for(MessageListener listener : messageListeners) {
				try {
					listener.onMessageReceived(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onNoticeSent(MessageEvent event) {
		synchronized(messageListeners) {
			for(MessageListener listener : messageListeners) {
				try {
					listener.onNoticeSent(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onIdentified(NickServEvent event) {
		synchronized(nickServListeners) {
			for(NickServListener listener : nickServListeners) {
				try {
					listener.onIdentified(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onNicknameChanged(NickServEvent event) {
		synchronized(nickServListeners) {
			for(NickServListener listener : nickServListeners) {
				try {
					listener.onNicknameChanged(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	public void addMessageListener(MessageListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(messageListeners) {
			messageListeners.add(listener);
		}
	}

	public boolean removeMessageListener(MessageListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(messageListeners) {
			return messageListeners.remove(listener);
		}
	}

	public void addNickServListener(NickServListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(nickServListeners) {
			nickServListeners.add(listener);
		}
	}

	public boolean removeNickServListener(NickServListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(nickServListeners) {
			return nickServListeners.remove(listener);
		}
	}

	@Override
	public void onIncomingFileTransfer(DCCEvent event) {
		synchronized(dccListeners) {
			for(DCCListener listener : dccListeners) {
				try {
					listener.onIncomingFileTransfer(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onIncomingChatRequest(DCCEvent event) {
		synchronized(dccListeners) {
			for(DCCListener listener : dccListeners) {
				try {
					listener.onIncomingChatRequest(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	@Override
	public void onFileTransferFinished(DCCEvent event) {
		synchronized(dccListeners) {
			for(DCCListener listener : dccListeners) {
				try {
					listener.onFileTransferFinished(event);
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		}
	}

	public void addDCCListener(DCCListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(dccListeners) {
			dccListeners.add(listener);
		}
	}

	public boolean removeDCCListener(DCCListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(dccListeners) {
			return dccListeners.remove(listener);
		}
	}
}
