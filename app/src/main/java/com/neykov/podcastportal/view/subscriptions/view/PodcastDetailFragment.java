package com.neykov.podcastportal.view.subscriptions.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.ToolbarViewFragment;
import com.neykov.podcastportal.view.subscriptions.PodcastDetailPresenter;
import com.squareup.picasso.Picasso;

public class PodcastDetailFragment extends ToolbarViewFragment<PodcastDetailPresenter> {

    public static final String TAG = PodcastDetailFragment.class.getSimpleName();
    private static final String ARG_TARGET_PODCAST = "PodcastDetailFragment.ARG_TARGET_PODCAST";

    public static PodcastDetailFragment newInstance(Subscription subscription){
        return newInstance((Podcast)subscription);
    }

    public static PodcastDetailFragment newInstance(Podcast podcast) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_PODCAST, podcast);
        PodcastDetailFragment fragment = new PodcastDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Podcast mTarget;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mLogoImageView;
    private TextView mDescriptionTextView;
    private TextView mWebsiteTextView;
    private TextView mSubscribersTextView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mTarget = getArguments().getParcelable(ARG_TARGET_PODCAST);
    }

    @NonNull
    @Override
    protected PodcastDetailPresenter onCreatePresenter() {
        return getDependencyResolver().getSubscriptionsComponent().createPodcastDetailPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_podcast_details, container, false);
        mLogoImageView = (ImageView) itemView.findViewById(R.id.logo);
        mDescriptionTextView = (TextView) itemView.findViewById(R.id.description);
        mSubscribersTextView = (TextView) itemView.findViewById(R.id.subscribers);
        mWebsiteTextView = (TextView) itemView.findViewById(R.id.website);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) itemView.findViewById(R.id.collapsingLayout);
        return itemView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindPodcast(mTarget);
        if (isTargetASubscription()) {
            bindSubscription((Subscription) mTarget);
        }
    }

    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void onConfigureToolbar(Toolbar toolbar) {
        super.onConfigureToolbar(toolbar);
        setHomeAsUpEnabled(true);
    }

    private void bindPodcast(Podcast podcast){
        mCollapsingToolbarLayout.setTitle(podcast.getTitle());
        mDescriptionTextView.setText(podcast.getDescription());
        mWebsiteTextView.setText(podcast.getWebsite());
        String subscribers = mSubscribersTextView.getResources()
                .getString(
                        R.string.label_subscribers_count_format,
                        podcast.getSubscribers());
        mSubscribersTextView.setText(subscribers);
        loadLogoImage(podcast);
    }

    private boolean isTargetASubscription(){
        return mTarget instanceof Subscription;
    }

    private void bindSubscription(Subscription subscription){

    }

    private void loadLogoImage(Podcast podcast) {
        if (podcast.getLogoUrl() != null) {
            Picasso.with(getContext())
                    .load(podcast.getLogoUrl())
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.photo_placeholder)
                    .into(mLogoImageView);
        }
    }
}
