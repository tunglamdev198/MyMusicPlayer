package com.lamnt.musicdemo;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout tb;

    private boolean isPlaying = false;

    private CardView mediaPlayerLayout;

    private ImageView btnPrevious;
    private ImageView btnNext;
    private ImageView btnPlayPause;
    private ImageView btnSearch;

    private TextView txtName;
    private TextView txtTittle;
    private EditText edtName;
    private TextView txtAuthor;
    private TextView txtTime;

    private SeekBar sbTime;
    private SeekBar sbVolume;

    private RecyclerView rvSongs;

    private SongAdapter adapter;

    private List<Song> songs;

    private Song currentSong;

    private SongPlayer songPlayer;

    private AudioManager audioManager;

    private int currentPosition;
    private int nextPosition;
    private int previousPosition;

    private int mediaDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        configVolume();
        registerListener();
        configRV(songs);
    }

    private void init() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        songPlayer = new SongPlayer();
        tb = findViewById(R.id.tb);
        rvSongs = findViewById(R.id.rv_songs);
        mediaPlayerLayout = findViewById(R.id.media_player);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnSearch = findViewById(R.id.btn_search);
        txtName = findViewById(R.id.txt_name);
        txtTittle = findViewById(R.id.txt_tittle);
        edtName = findViewById(R.id.edt_name);
        txtAuthor = findViewById(R.id.txt_author);
        txtTime = findViewById(R.id.txt_time);
        sbTime = findViewById(R.id.sb_time);
        sbVolume = findViewById(R.id.sb_volume);
        songs = SongUtil.getSong(this);
    }

    private void configVolume() {
        sbVolume.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });
    }

    private void registerListener() {
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    private void configRV(final List<Song> songs) {
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this,
//                LinearLayoutManager.VERTICAL);
//        rvSongs.addItemDecoration(decoration);
        adapter = new SongAdapter(this, songs);
        rvSongs.setAdapter(adapter);
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                songPlayer.stop();
                currentPosition = position;
                isPlaying = true;
                currentSong = songs.get(position);
                mediaPlayerLayout.setVisibility(View.VISIBLE);
                fetchData();
                initState();
                songPlayer.play(MainActivity.this, currentSong.getData());
                sbTime.setMax(songPlayer.getMaxDuration());
                txtTime.setText(generateTime(songPlayer.getMaxDuration()));
            }
        });
    }

    private void initState() {
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private String generateTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        time += min + ":";
        if (sec < 10) {
            time += "0" + sec;
        } else {
            time += sec;
        }
        return time;
    }

    private void search() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals("")) {
                    return;
                }
                List<Song> filters = findSongByKey(s.toString());
                configRV(filters);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    btnSearch.setOnClickListener(MainActivity.this);
                    txtTittle.setVisibility(View.VISIBLE);
                    edtName.setVisibility(View.GONE);
                    InputMethodManager manager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(edtName.getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    handle = true;
                }
                return handle;
            }
        });
    }

    private List<Song> findSongByKey(String key) {
        List<Song> results = new ArrayList<>();
        for (Song song : songs) {
            if (song.getName().toLowerCase().contains(key.toLowerCase())) {
                results.add(song);
            }
        }
        return results;
    }

    private void setUpState() {
        if (isPlaying) {
            songPlayer.resume();
        } else {
            songPlayer.pause();
        }
    }

    private void fetchData() {
        txtName.setText(currentSong.getName());
        txtAuthor.setText(currentSong.getArtist());
        txtTime.setText(generateTime(currentSong.getTime()));
    }

    private void previousSong() {
        previousPosition = currentPosition - 1;
        if (previousPosition == -1) {
            Toast.makeText(this, "Đã là bài hát đầu tiên",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        songPlayer.stop();
        currentPosition = previousPosition;
        currentSong = songs.get(previousPosition);
        isPlaying = true;
        songPlayer.play(this, currentSong.getData());
        fetchData();
        initState();
    }

    private void nextSong() {
        nextPosition = currentPosition + 1;
        if (nextPosition >= songs.size()) {
            Toast.makeText(this, "Đã là bài hát cuối cùng",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        songPlayer.stop();
        currentPosition = nextPosition;
        currentSong = songs.get(nextPosition);
        isPlaying = true;
        songPlayer.play(this, currentSong.getData());
        fetchData();
        initState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                nextSong();
                break;

            case R.id.btn_play_pause:
                isPlaying = !isPlaying;
                setUpState();
                initState();
                break;
            case R.id.btn_previous:
                previousSong();
                break;
            case R.id.btn_search:
                txtTittle.setVisibility(View.GONE);
                edtName.setVisibility(View.VISIBLE);
                search();
                btnSearch.setOnClickListener(null);
                break;
        }
    }
}
