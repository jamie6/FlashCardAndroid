package com.example.flashcards;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;
import java.util.List;

public class ViewDeckFragment extends Fragment {
    private long duration = 500;
    private UpdateCardTextRunnable updateCardTextRunnable;
    private List<Animator> movingRightAnimatorListFront, movingLeftAnimatorListFront, movingRightAnimatorListBack, movingLeftAnimatorListBack;
    private AnimatorSet flipFirstHalf, flipSecondHalf, moveCardFrontLeft, moveCardBackLeft, moveCardFrontRight, moveCardBackRight;
    private String deckName;
    private Card[] deck;
    private int index;
    private boolean showTerm;
    private View cardViewFront, cardViewBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deckName = getArguments().getString("deckName");
        deck = DeckManager.getDeckFromFile(deckName);
        index = 0;
        showTerm = false;
        updateCardTextRunnable = new UpdateCardTextRunnable();
        return inflater.inflate(R.layout.fragment_view_deck, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getContext();
        cardViewFront = view.findViewById(R.id.card_view_front);
        cardViewBack = view.findViewById(R.id.card_view_back);

        cardViewFront.setOnTouchListener(new OnSwipeTouchListener(context));
        cardViewBack.setOnTouchListener(new OnSwipeTouchListener(context));

        updateCard();
        loadAnimations();
        changeCameraDistance();
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        cardViewFront.setCameraDistance(scale);
        cardViewBack.setCameraDistance(scale);
    }

    private void loadAnimations() {
        initializeAnimationObjects();
        loadFlipAnimations();
    }

    private void initializeAnimationObjects() {
        moveCardFrontLeft = new AnimatorSet();
        moveCardBackLeft = new AnimatorSet();
        moveCardFrontRight = new AnimatorSet();
        moveCardBackRight = new AnimatorSet();
        movingLeftAnimatorListFront = new LinkedList<>();
        movingRightAnimatorListFront = new LinkedList<>();
        movingLeftAnimatorListBack = new LinkedList<>();
        movingRightAnimatorListBack = new LinkedList<>();
    }

    private void loadFlipAnimations() {
        flipFirstHalf = (AnimatorSet)AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip1);
        flipSecondHalf = (AnimatorSet)AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip2);
    }

    private void loadLeftMovingAnimation() {
        ObjectAnimator moveOutLeftFront, moveInFront, teleportToCenterFront, teleportToRightOfScreenFront,
                moveOutLeftBack, moveInBack, teleportToCenterBack, teleportToRightOfScreenBack;

        float rightSidePosition = getResources().getDisplayMetrics().widthPixels;
        float centerPosition = cardViewFront.getTranslationX();
        float leftSidePosition = centerPosition - rightSidePosition;

        moveOutLeftFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", leftSidePosition);
        moveOutLeftFront.setDuration(duration);

        moveInFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", centerPosition);
        moveInFront.setDuration(duration);

        teleportToRightOfScreenFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", rightSidePosition);
        teleportToRightOfScreenFront.setDuration(0);

        teleportToCenterFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", centerPosition);
        teleportToCenterFront.setDuration(0);

        moveOutLeftBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", leftSidePosition);
        moveOutLeftBack.setDuration(duration);

        moveInBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", centerPosition);
        moveInBack.setDuration(duration);

        teleportToRightOfScreenBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", rightSidePosition);
        teleportToRightOfScreenBack.setDuration(0);

        teleportToCenterBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", centerPosition);
        teleportToCenterBack.setDuration(0);

        movingLeftAnimatorListFront.clear();
        movingLeftAnimatorListFront.add(teleportToCenterFront);
        movingLeftAnimatorListFront.add(moveOutLeftFront);
        movingLeftAnimatorListFront.add(teleportToRightOfScreenFront);
        movingLeftAnimatorListFront.add(moveInFront);

        movingLeftAnimatorListBack.clear();
        movingLeftAnimatorListBack.add(teleportToCenterBack);
        movingLeftAnimatorListBack.add(moveOutLeftBack);
        movingLeftAnimatorListBack.add(teleportToRightOfScreenBack);
        movingLeftAnimatorListBack.add(moveInBack);

        moveCardFrontLeft.playSequentially(movingLeftAnimatorListFront);
        moveCardBackLeft.playSequentially(movingLeftAnimatorListBack);
    }

    private void loadRightMovingAnimation() {
        ObjectAnimator moveOutRightFront, moveInFront, teleportToCenterFront, teleportToLeftOfScreenFront,
                moveOutRightBack, moveInBack, teleportToCenterBack, teleportToLeftOfScreenBack;

        float rightSidePosition = getResources().getDisplayMetrics().widthPixels;
        float centerPosition = cardViewFront.getTranslationX();
        float leftSidePosition = centerPosition - rightSidePosition;

        moveOutRightFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", rightSidePosition);
        moveOutRightFront.setDuration(duration);

        moveInFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", centerPosition);
        moveInFront.setDuration(duration);

        teleportToLeftOfScreenFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", leftSidePosition);
        teleportToLeftOfScreenFront.setDuration(0);

        teleportToCenterFront = ObjectAnimator.ofFloat(cardViewFront, "translationX", centerPosition);
        teleportToCenterFront.setDuration(0);

        moveOutRightBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", rightSidePosition);
        moveOutRightBack.setDuration(duration);

        moveInBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", centerPosition);
        moveInBack.setDuration(duration);

        teleportToLeftOfScreenBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", leftSidePosition);
        teleportToLeftOfScreenBack.setDuration(0);

        teleportToCenterBack = ObjectAnimator.ofFloat(cardViewBack, "translationX", centerPosition);
        teleportToCenterBack.setDuration(0);

        movingRightAnimatorListFront.clear();
        movingRightAnimatorListFront.add(teleportToCenterFront);
        movingRightAnimatorListFront.add(moveOutRightFront);
        movingRightAnimatorListFront.add(teleportToLeftOfScreenFront);
        movingRightAnimatorListFront.add(moveInFront);

        movingRightAnimatorListBack.clear();
        movingRightAnimatorListBack.add(teleportToCenterBack);
        movingRightAnimatorListBack.add(moveOutRightBack);
        movingRightAnimatorListBack.add(teleportToLeftOfScreenBack);
        movingRightAnimatorListBack.add(moveInBack);

        moveCardFrontRight.playSequentially(movingRightAnimatorListFront);
        moveCardBackRight.playSequentially(movingRightAnimatorListBack);
    }

    private void updateCard() {
        TextView cardViewFrontTextView = cardViewFront.findViewById(R.id.card_view_text);
        cardViewFrontTextView.setText(deck[index].getTerm());

        TextView cardViewBackTextView = cardViewBack.findViewById(R.id.card_view_text);
        cardViewBackTextView.setText(deck[index].getDefinition());
    }

    private void decrementDeckIndex() {
        index--;
        showTerm = true;
        if (index < 0) {
            index = deck.length - 1;
        }
    }

    private void incrementDeckIndex() {
        index++;
        showTerm = true;
        if (index == deck.length) {
            index = 0;
        }
    }

    private void flipCard() {
        if (showTerm) {
            flipFirstHalf.setTarget(cardViewBack);
            flipSecondHalf.setTarget(cardViewFront);
        } else {
            flipFirstHalf.setTarget(cardViewFront);
            flipSecondHalf.setTarget(cardViewBack);
        }
        showTerm = !showTerm;
        flipFirstHalf.start();
        flipSecondHalf.start();
    }

    private void nextCard() {
        loadLeftMovingAnimation();
        incrementDeckIndex();
        moveCardFrontLeft.start();
        moveCardBackLeft.start();
        updateCard();
        //updateCardTextRunnable.start();
    }

    private void previousCard() {
        loadRightMovingAnimation();
        decrementDeckIndex();
        moveCardFrontRight.start();
        moveCardBackRight.start();
        updateCard();
        //updateCardTextRunnable.start();
    }

    private class UpdateCardTextRunnable extends Thread {
        @Override
        public void run() {
            try {
                sleep(duration);
                updateCard();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
            // get previous card, card moving right
            Log.d("touchcard", "swipe right");
            previousCard();
        }

        public void onSwipeLeft() {
            // get next card, card moving left
            Log.d("touchcard", "swipe left");
            nextCard();
        }

        public void onSwipeTop() {
            Log.d("touchcard", "swipe top");
            flipCard();
        }

        public void onSwipeBottom() {
            Log.d("touchcard", "swipe bottom");
            flipCard();
        }
    }
}
