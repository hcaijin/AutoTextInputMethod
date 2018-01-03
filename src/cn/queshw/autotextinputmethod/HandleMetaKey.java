package cn.queshw.autotextinputmethod;

import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
//���������ʵ�ּ����ϵĹ��ܼ��Ĺ��ܣ���Ҫ˼·����һ������������¼��Щ���ܼ���״̬�������Ƿ񱻰��£��Ƿ������ȵȡ�����ʵ�ʼ��̲����ַ��Ĺ����У�ʹ�õ���һ�����͵�״̬���
//Ҳ����˵�����͵ĺ��ĸ��ֽڷŵľ���������״̬��ǣ�ǰ�ĸ��ֽھ�����������ʵ�ֶ��⹦�ܵĻ��߼�¼֮ǰ����״̬�ĵط�
//��һ�������У�Ӧ���ȶ���һ������������¼���̹��ܼ���״̬��Ȼ���ڰ����Ĺ������ñ����еķ������������ܼ��ı��״̬��Ȼ���ڳ�������ʹ����Щ�����ʵ�ֶ�Ӧ�Ĺ��ܡ�
//������и����ĸ����ܼ���״̬����alt����shift����shift��sym�����Ǳ仯�Ĺ�����������
//1��������clear��״̬��������º󲻷ţ���Ϊpressed״̬
//3���ɿ��������֮ǰ��pressed״̬������released״̬�����֮ǰ��used״̬����գ���Ӧ�ļ��ı��λ��on״̬��
//4�������ʱ�ٰ��ù��ܼ������֮ǰ��released״̬������locked״̬�����֮ǰ��locked״̬���ٰ��ü��������״̬
//��һ�������ķǹ��ܼ������֮ǰ��pressed����used״̬�����֮ǰ��released���Ǿ����
//pressed��used״̬����ʾһֱ���Ź��ܼ�û���ɿ��ء�used��ʾ������һֱ���Ź��ܼ��Ĺ����У����������ķǹ��ܼ���
//sym���������ԣ���û��locked״̬��������Ҫһ�����λ����ʾ����С����Ҫ��ҳ

public class HandleMetaKey {

	public static final int META_CAP_ON = KeyEvent.META_SHIFT_RIGHT_ON;
	public static final int META_ALT_ON = KeyEvent.META_ALT_ON;
	public static final int META_SYM_ON = KeyEvent.META_SYM_ON;
	public static final int META_NEWSIM_ON = KeyEvent.META_SHIFT_LEFT_ON;// �Զ����ctrl����Ҳ������shift��

	public static final int META_CAP_LOCKED = 0x100;
	public static final int META_ALT_LOCKED = 0x200;
	public static final int META_SYM_LOCKED = 0x400;
	public static final int META_NEWSIM_LOCKED = 0x800;	

	private static final long META_CAP_USED = 1L << 32;
	private static final long META_ALT_USED = 1L << 33;
	private static final long META_SYM_USED = 1L << 34;
	private static final long META_NEWSIM_USED = 1L << 35;
	
	private static final long META_CAP_LOCK_RELEASED = 1L << 36;
	private static final long META_ALT_LOCK_RELEASED = 1L << 37;
	private static final long META_SYM_LOCK_RELEASED = 1L << 38;
	private static final long META_NEWSIM_LOCK_RELEASED = 1L << 39;

	private static final long META_CAP_PRESSED = 1L << 40;
	private static final long META_ALT_PRESSED = 1L << 41;
	private static final long META_SYM_PRESSED = 1L << 42;
	private static final long META_NEWSIM_PRESSED = 1L << 43;

	private static final long META_CAP_RELEASED = 1L << 48;
	private static final long META_ALT_RELEASED = 1L << 49;
	public static final long META_SYM_RELEASED = 1L << 50;
	private static final long META_NEWSIM_RELEASED = 1L << 51;
	
	public static final long META_SYM_TURNPAGE = 1L << 52;// ���ڱ�Ǳ���С����Ҫ��ҳ���ڷ�ҳ�����


	private static final long META_ALL = META_CAP_ON | META_CAP_LOCKED | META_CAP_USED | META_CAP_PRESSED | META_CAP_RELEASED | META_ALT_ON
			| META_ALT_LOCKED | META_ALT_USED | META_ALT_PRESSED | META_ALT_RELEASED | META_SYM_ON | META_SYM_LOCKED | META_SYM_USED
			| META_SYM_PRESSED | META_SYM_RELEASED | META_NEWSIM_ON | META_NEWSIM_LOCKED | META_NEWSIM_USED | META_NEWSIM_PRESSED
			| META_NEWSIM_RELEASED | META_SYM_TURNPAGE | META_CAP_LOCK_RELEASED | META_ALT_LOCK_RELEASED | META_SYM_LOCK_RELEASED 
			| META_NEWSIM_LOCK_RELEASED;

	private static final long META_CAP_MASK = META_ALL;
	private static final long META_ALT_MASK = META_ALL;
	private static final long META_SYM_MASK = META_ALL;
	private static final long META_NEWSIM_MASK = META_ALL;

//	private static final int CLEAR_RETURN_VALUE = 0;
//	private static final int PRESSED_RETURN_VALUE = 1;
//	private static final int LOCKED_RETURN_VALUE = 2;

	// ���ڴ�state�л��metastate�����metastate�뵱ǰ�ļ����¼��д���metastate��ͬ�����ǵ��ۺ�Ч���������յ�metastate��Ӧ�������ã�
	// mMetaState = event.getMetaState() | listener.getMetaState()
	// ��long state�л�ù��ܼ���״̬��ֻ����ĸ���Ŷ
	public static final int getMetaState(long state) {
		int result = 0;

		if ((state & (META_CAP_LOCKED | META_CAP_LOCK_RELEASED)) != 0) {
			result |= META_CAP_LOCKED | META_CAP_ON;
		} else if ((state & META_CAP_ON) != 0) {
			result |= META_CAP_ON;
		}

		if ((state & (META_ALT_LOCKED | META_ALT_LOCK_RELEASED)) != 0) {
			result |= META_ALT_LOCKED | META_ALT_ON;
		} else if ((state & META_ALT_ON) != 0) {
			result |= META_ALT_ON;
		}

		if ((state & (META_SYM_LOCKED | META_SYM_LOCK_RELEASED)) != 0) {
			result |= META_SYM_LOCKED | META_SYM_ON;
		} else if ((state & META_SYM_ON) != 0) {
			result |= META_SYM_ON;
		}

		if ((state & (META_NEWSIM_LOCKED | META_NEWSIM_LOCK_RELEASED)) != 0) {
			result |= META_NEWSIM_LOCKED | META_NEWSIM_ON;
		} else if ((state & META_NEWSIM_ON) != 0) {
			result |= META_NEWSIM_ON;
		}
		
		if ((state & META_SYM_TURNPAGE) != 0) {//���ڻ�ȡ������̷�ҳ��״̬ 
			result |= META_SYM_TURNPAGE;
		}

		return result;
	}

//	// �����ж���state�У�ĳ������״̬��������״̬�������Ѿ����µ�״̬�������Ѿ������
//	// ע�⣺ֻ�ܼ�����ּ�CAP ALT SYM NEWSIM
//	public static final int getMetaState(long state, int meta) {
//		switch (meta) {
//		case META_CAP_ON:
//			if ((state & META_CAP_LOCKED) != 0)
//				return LOCKED_RETURN_VALUE;
//			if ((state & META_CAP_ON) != 0)
//				return PRESSED_RETURN_VALUE;
//			return CLEAR_RETURN_VALUE;
//
//		case META_ALT_ON:
//			if ((state & META_ALT_LOCKED) != 0)
//				return LOCKED_RETURN_VALUE;
//			if ((state & META_ALT_ON) != 0)
//				return PRESSED_RETURN_VALUE;
//			return CLEAR_RETURN_VALUE;
//
//		case META_SYM_ON:
//			if ((state & META_SYM_LOCKED) != 0)
//				return LOCKED_RETURN_VALUE;
//			if ((state & META_SYM_ON) != 0)
//				return PRESSED_RETURN_VALUE;
//			return CLEAR_RETURN_VALUE;
//
//		case META_NEWSIM_ON:
//			if ((state & META_NEWSIM_LOCKED) != 0)
//				return LOCKED_RETURN_VALUE;
//			if ((state & META_NEWSIM_ON) != 0)
//				return PRESSED_RETURN_VALUE;
//			return CLEAR_RETURN_VALUE;
//
//		default:
//			return CLEAR_RETURN_VALUE;
//		}
//	}

	// �����һ����Ҫ�ĺ��������ڴ���CAP ALT SYM NEWSIM�����º�state״̬�ĵ���
	public static long handleKeyDown(long state, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {// �����ǰ�ļ����¼�����shift������
			return press(state, META_CAP_ON, META_CAP_MASK, META_CAP_LOCKED, META_CAP_PRESSED, META_CAP_RELEASED, META_CAP_LOCK_RELEASED,META_CAP_USED);
		}

		if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT || keyCode == KeyEvent.KEYCODE_NUM) {// �����ǰ�ļ����¼�����alt������
			return press(state, META_ALT_ON, META_ALT_MASK, META_ALT_LOCKED, META_ALT_PRESSED, META_ALT_RELEASED, META_ALT_LOCK_RELEASED, META_ALT_USED);
		}

		if (keyCode == KeyEvent.KEYCODE_SYM) {// �����ǰ�ļ����¼�����sym������
			return press(state, META_SYM_ON, META_SYM_MASK, META_SYM_LOCKED, META_SYM_PRESSED, META_SYM_RELEASED, META_SYM_LOCK_RELEASED,META_SYM_USED);
		}

		if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {// �����ǰ�ļ����¼�����shift������
			return press(state, META_NEWSIM_ON, META_NEWSIM_MASK, META_NEWSIM_LOCKED, META_NEWSIM_PRESSED, META_NEWSIM_RELEASED, META_NEWSIM_LOCK_RELEASED, META_NEWSIM_USED);
		}

		return state;
	}

	// �����������ܼ������º��״̬�����������
	// state ��ǰ���ܼ���״̬
	// what ��ǰ�����µĹ��ܼ����ĸ�
	// mask locked pressed released used ��Щ���ǹ��ܼ���Ӧ��״̬��ʱ��ı��λ
	// ��pressed ��used ״̬��ʱ��ͬʱ��Ӧ��on״̬�϶����Ѿ���ǵ�
	private static long press(long state, int what, long mask, long locked, long pressed, long released, long lock_released, long used) {
		if ((state & pressed) != 0) {
			// ��ʾ֮ǰ�Ѿ����������ܼ��������ˣ�Ҳ����˵����ֻ���ظ��¼���Ҳ���ǲ����߰���������ܼ����š�
			// ����Ҫ��������Ȼ�Ǳ��������ܼ��������ˣ���pressed
			// repeat before use
			//Log.d("Here", "pressed Repeating����");
		} else if ((state & released) != 0) {
			// ���֮ǰ�ı�Ǵ˹��ܼ�������֮�����ͷ��ˣ������ְ��£�˵���������ǰ�һ��֮���ְ���һ�£��Ǿ���������
			// ͬʱ����������ܼ���״̬
			if (what == META_SYM_ON) {//sym��û������״̬��ֻ���Ҫ�������Ҫ��ҳ
				//state = (state & ~mask) | what | pressed | META_SYM_TURNPAGE;
				state = (state & ~mask) | what | released;
			} else {
				state = (state & ~mask) | what | locked;
				//Log.d("Here", "released to locked!");
				//Log.d("Here", "released to locked! = " + String.valueOf(state));
			}			
		} else if ((state & used) != 0) {
			// ���֮ǰ��ǹ��ܼ��Ѿ�ʹ�ù��ˣ����ǲ����߻��ǰ��Ź��ܼ�
			// repeat after use
			//Log.d("Here", " Repeating after used����!");
		} else if ((state & locked) != 0) {
			// ���֮ǰ�ı����ʾ�˹��ܼ��Ѿ���������ô�ٰ��˹��ܼ���Ȼ������״̬
			//Log.d("Here", "locked repeating����!");			
		} else if ((state & lock_released) != 0) {
				// ���֮ǰ�ı����ʾ�˹��ܼ��Ѿ��������������ɿ�����ô�ٰ��˹��ܼ����ǽ�����
				//Log.d("Here", " lock_released to clear!");
				state &= ~mask;
		} else {
			// ���������������Ǿ��Ǳ�ǹ��ܼ��������ˣ�ͬʱ��whatҲ�洢���������´�
			//Log.d("Here", " clear to pressed!");
			state &= ~mask;
			state |= what | pressed;
			if (what == META_SYM_ON){
				//Log.d("Here", "turn on META_SYM_TURNPAGE");
				state |= META_SYM_TURNPAGE;
			}
		}
		return state;
	}

	// ��Ҳ��һ����Ҫ���������ڴ���CAP ALT SYM NEWSIM�ͷź�state״̬�ĵ���
	public static long handleKeyUp(long state, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
			return release(state, META_CAP_ON, META_CAP_MASK, META_CAP_PRESSED, META_CAP_RELEASED, META_CAP_LOCKED, META_CAP_LOCK_RELEASED, META_CAP_USED, event);
		}

		if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT || keyCode == KeyEvent.KEYCODE_NUM) {
			return release(state, META_ALT_ON, META_ALT_MASK, META_ALT_PRESSED, META_ALT_RELEASED, META_ALT_LOCKED, META_ALT_LOCK_RELEASED, META_ALT_USED, event);
		}

		if (keyCode == KeyEvent.KEYCODE_SYM) {
			return release(state, META_SYM_ON, META_SYM_MASK, META_SYM_PRESSED, META_SYM_RELEASED, META_SYM_LOCKED, META_SYM_LOCK_RELEASED, META_SYM_USED, event);
		}

		if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
			return release(state, META_NEWSIM_ON, META_NEWSIM_MASK, META_NEWSIM_PRESSED, META_NEWSIM_RELEASED, META_NEWSIM_LOCKED, META_NEWSIM_LOCK_RELEASED, META_NEWSIM_USED, event);
		}

		return state;
	}

	private static long release(long state, int what, long mask, long pressed, long released, long locked, long lock_released, long used, KeyEvent event) {
		//Log.d("Here", "before release state = " + String.valueOf(state));
		switch (event.getKeyCharacterMap().getModifierBehavior()) {
		case KeyCharacterMap.MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED:			
			if ((state & used) != 0) {
				// ���֮ǰ�����ʾ�˹��ܼ��Ѿ�ʹ�ù��ˣ������ͷ������״̬
				//Log.d("Here"," used to clear!");
				state &= ~mask;
			} else if ((state & pressed) != 0) {
				// ���֮ǰ��Ǳ����µ�״̬�������ͷ����ʾ�ǵ�һ�ΰ���Щ���ܼ�������Ϊ�ͷţ�ͬʱ��what����ȥ
				//Log.d("Here", " pressed to released!");
				state &= ~mask;
				state |= what | released;
			} else if ((state & locked) != 0) {
				// ���֮ǰ��Ǳ����µ�״̬�������ͷ����ʾ�ǵ�һ�ΰ���Щ���ܼ�������Ϊ�ͷţ�ͬʱ��what����ȥ
				//Log.d("Here", " locked to lock_released!");
				state &= ~mask;
				state |= what | lock_released;
			}
			else if ((state & released) != 0) {	
				if (what == META_SYM_ON){
					//Log.d("Here", "turn on META_SYM_TURNPAGE");
					state |= META_SYM_TURNPAGE;
				}
			}
			break;

		default:
			state &= ~mask;
			//Log.d("Here", " default to clear!");
			break;
		}
		return state;
	}

	// �����ڷǹ��ܼ������¼�����֮��state״̬�ĵ���.��ֻӦ����CAP ALT SYM NEWSIM֮����¼�������󣬲ŵ����������
	public static long adjustMetaAfterKeypress(long state) {
		if ((state & META_CAP_PRESSED) != 0) {
			// ��ʾ֮ǰ���ܼ��Ѿ��������ˣ�����û���ͷţ�˵��������һֱ���Ź��ܼ�����ô�ͱ�Ǵ˹��ܼ��Ѿ�ʹ�ã�ͬʱ������
			state = (state & ~META_CAP_MASK) | META_CAP_ON | META_CAP_USED;
			// Log.d("Here", "CAP pressed to used!");
		} else if ((state & META_CAP_RELEASED) != 0) {
			// ��ʾ֮ǰ���ܼ��Ѿ�����Ȼ���ֱ��ͷţ��������������������¼���˵���˹��ܼ���״̬�Ѿ�Ҫ����ˡ�
			state &= ~META_CAP_MASK;
			// Log.d("Here", "CAP released to clear!");
		}

		if ((state & META_ALT_PRESSED) != 0) {
			state = (state & ~META_ALT_MASK) | META_ALT_ON | META_ALT_USED;
			// Log.d("Here", "ALT pressed to used!");
		} else if ((state & META_ALT_RELEASED) != 0) {
			state &= ~META_ALT_MASK;
			// Log.d("Here", "ALT released to clear!");
		}

		if ((state & META_SYM_PRESSED) != 0) {
			state = (state & ~META_SYM_MASK) | META_SYM_ON | META_SYM_USED;
			// Log.d("Here", "SYM pressed to used!");
		} else if ((state & META_SYM_RELEASED) != 0) {
			// Log.d("Here", "SYM released to clear!");
			state &= ~META_SYM_MASK;
		}

		if ((state & META_NEWSIM_PRESSED) != 0) {
			state = (state & ~META_NEWSIM_MASK) | META_NEWSIM_ON | META_NEWSIM_USED;
			// Log.d("Here", "NEWSIM pressed to used!");
		} else if ((state & META_NEWSIM_RELEASED) != 0) {
			state &= ~META_NEWSIM_MASK;
			// Log.d("Here", "NEWSIM released to clear!");
		}
		return state;
	}

	// ���state�е�ĳЩ����״ֵ̬
	public static long clearMetaKeyState(long state, int which) {
		if ((which & META_CAP_ON) != 0 && (state & META_CAP_LOCKED) != 0) {
			state &= ~META_CAP_MASK;
		}
		if ((which & META_ALT_ON) != 0 && (state & META_ALT_LOCKED) != 0) {
			state &= ~META_ALT_MASK;
		}
		if ((which & META_SYM_ON) != 0 && (state & META_SYM_LOCKED) != 0) {
			state &= ~META_SYM_MASK;
		}
		if ((which & META_NEWSIM_ON) != 0 && (state & META_NEWSIM_LOCKED) != 0) {
			state &= ~META_NEWSIM_MASK;
		}
		return state;
	}
}
