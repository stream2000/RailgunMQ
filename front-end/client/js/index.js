//scripts for initiating the index page

$(function() {
  if (sessionStorage.getItem('rankBoard') === null) {
    var rankBoard = [{
        'src': 'images/rank1.jpg',
        'name': '每日播放量排行',
        'id': 'play 1',
      },
      {
        'src': 'images/rank2.jpg',
        'name': '三日播放量排行',
        'id': 'play 3',
      },
      {
        'src': 'images/rank3.jpg',
        'name': '七日播放量排行',
        'id': 'play 7',
      },
      {
        'src': 'images/rank4.jpg',
        'name': '每日点赞量排行',
        'id': 'like 1',
      },
      {
        'src': 'images/rank5.jpg',
        'name': '三日点赞量排行',
        'id': 'like 3',
      },
      {
        'src': 'images/rank6.jpg',
        'name': '七日点赞量排行',
        'id': 'like 7',
      },
    ];
    sessionStorage.setItem('rankBoard', JSON.stringify(rankBoard));
  }
  if (sessionStorage.getItem('langBoard') === null) {
    var langBoard = [{
        'src': 'images/jj.jpg',
        'name': '华语',
        'id': 'lang Mar',
      },
      {
        'src': 'images/oasis.jpg',
        'name': '欧美',
        'id': 'lang Eng',
      },
      {
        'src': 'images/japan.jpg',
        'name': '日语',
        'id': 'lang Jap',
      },
      {
        'src': 'images/korea.jpg',
        'name': '韩语',
        'id': 'lang Kor',
      },
      {
        'src': 'images/dura.jpg',
        'name': '小语种',
        'id': 'lang Min',
      },
    ];
    sessionStorage.setItem('langBoard', JSON.stringify(langBoard));
  }
  if (sessionStorage.getItem('styleBoard') === null) {
    var styleBoard = [{
        'src': 'images/pop.jpg',
        'name': '流行',
        'id': 'style pop',
      },
      {
        'src': 'images/rock.jpg',
        'name': '摇滚',
        'id': 'style rock',
      },
      {
        'src': 'images/folk.jpg',
        'name': '民谣',
        'id': 'style folk',
      },
      {
        'src': 'images/electronic.jpg',
        'name': '电子',
        'id': 'style elec',
      },
      {
        'src': 'images/rap.jpg',
        'name': '说唱',
        'id': 'style rap',
      },
      {
        'src': 'images/country.jpg',
        'name': '乡村',
        'id': 'style coun',
      },
      {
        'src': 'images/blues.jpg',
        'name': '蓝调',
        'id': 'style blues',
      },
      {
        'src': 'images/ancient.jpg',
        'name': '古风',
        'id': 'style anci',
      },
    ];
    sessionStorage.setItem('styleBoard', JSON.stringify(styleBoard));
  }
  if (sessionStorage.getItem('emoBoard') === null) {
    var emoBoard = [{
        'src': 'images/nostalgia.jpg',
        'name': '怀旧',
        'id': 'emo nost',
      },
      {
        'src': 'images/fresh.jpg',
        'name': '清新',
        'id': 'emo fresh',
      },
      {
        'src': 'images/love.jpg',
        'name': '浪漫',
        'id': 'emo roma',
      },
      {
        'src': 'images/sorrow.jpg',
        'name': '悲伤',
        'id': 'emo sorr',
      },
      {
        'src': 'images/cure.jpg',
        'name': '治愈',
        'id': 'emo cure',
      },
      {
        'src': 'images/release.jpg',
        'name': '轻松',
        'id': 'emo rele',
      },
      {
        'src': 'images/isolation.jpg',
        'name': '孤独',
        'id': 'emo lonely',
      },
      {
        'src': 'images/joy.jpg',
        'name': '快乐',
        'id': 'emo joy',
      },
      {
        'src': 'images/miss_main.jpg',
        'name': '思念',
        'id': 'emo miss',
      },
    ];
    sessionStorage.setItem('emoBoard', JSON.stringify(emoBoard));
  }
  addRankItem();
  addlangItem();
  addstyleItem();
  addemoItem();
})

function addRankItem() {
  $('#rankItems').empty();
  $('#ranktitle').children('h3').remove();
  $('#ranktitle').children('a').remove();
  var rankBoard = JSON.parse(sessionStorage.getItem('rankBoard'));
  for (rankItem of rankBoard) {
    $('#rankItems').append('<div class="col-lg-4 col-md-6 col-sm-12" style="position:relative;"><div class="package-one"><div class="img-wapper">' +
      '<img src="' + rankItem['src'] + '" alt="package-img"></div><div class="package-content" align="center"><h3>' + rankItem['name'] + '</h3><ul class="ct-action">' +
      '<li><a id="' + rankItem['id'] + '" href="javascript:void(0);" onclick="rank($(this).attr(\'id\'))" class="booking-btn">查看</a></li><li></li></ul></div></div></div>');
  }
}

function addlangItem() {
  $('#langItem').empty();
  $('#langtitle').children('h3').remove();
  $('#langtitle').children('a').remove();
  var langBoard = JSON.parse(sessionStorage.getItem('langBoard'));
  for (recomItem of langBoard) {
    $('#langItem').append('<div class="item"><div class="place-box"><div class="wapper-img"><img src="' + recomItem['src'] + '" alt="">' +
      '<a type="' + recomItem['id'] + '" href="javascript:void(0);" onclick="recommend($(this).attr(\'type\'))" class="pop-btn">查看</a></div>' +
      '<div class="desi-inner" align="center"><h4>' + recomItem['name'] + '</h4></div></div></div>');
  }
  $('#langItem').slick({
    infinite: true,
    slidesToShow: 3,
    slidesToScroll: 1,
    dots: true,
    autoplay: true,
    responsive: [{
      breakpoint: 992,
      settings: {
        slidesToShow: 2
      }
    }, {
      breakpoint: 768,
      settings: {
        slidesToShow: 1
      }
    }]
  });
}

function addstyleItem(type) {
  $('#styleItem').empty();
  $('#styletitle').children('h3').remove();
  $('#styletitle').children('a').remove();
  var styleBoard = JSON.parse(sessionStorage.getItem('styleBoard'));
  for (recomItem of styleBoard) {
    $('#styleItem').append('<div class="item"><div class="place-box"><div class="wapper-img"><img src="' + recomItem['src'] + '" alt="">' +
      '<a type="' + recomItem['id'] + '" href="javascript:void(0);" onclick="recommend($(this).attr(\'type\'))" class="pop-btn">查看</a></div>' +
      '<div class="desi-inner" align="center"><h4>' + recomItem['name'] + '</h4></div></div></div>');
  }
  $('#styleItem').slick({
    infinite: true,
    slidesToShow: 3,
    slidesToScroll: 1,
    dots: true,
    autoplay: true,
    responsive: [{
      breakpoint: 992,
      settings: {
        slidesToShow: 2
      }
    }, {
      breakpoint: 768,
      settings: {
        slidesToShow: 1
      }
    }]
  });
}

function addemoItem() {
  $('#emoItem').empty();
  $('#emotitle').children('h3').remove();
  $('#emotitle').children('a').remove();
  var emoBoard = JSON.parse(sessionStorage.getItem('emoBoard'));
  for (recomItem of emoBoard) {
    $('#emoItem').append('<div class="item"><div class="place-box"><div class="wapper-img"><img src="' + recomItem['src'] + '" alt="">' +
      '<a type="' + recomItem['id'] + '" href="javascript:void(0);" onclick="recommend($(this).attr(\'type\'))" class="pop-btn">查看</a></div>' +
      '<div class="desi-inner" align="center"><h4>' + recomItem['name'] + '</h4></div></div></div>');
  }
  $('#emoItem').slick({
    infinite: true,
    slidesToShow: 3,
    slidesToScroll: 1,
    dots: true,
    autoplay: true,
    responsive: [{
      breakpoint: 992,
      settings: {
        slidesToShow: 2
      }
    }, {
      breakpoint: 768,
      settings: {
        slidesToShow: 1
      }
    }]
  });
}

function rank(type) {
  var tags = type.split(' ');
  var target = tags[0];
  var period_length = tags[1];

  $.ajax({
    type: 'get',
    dataType: 'json',
    contentType: 'appliaction/json;charset=utf-8',
    url: 'api/RankingList/' + target+'s/'+period_length,
    success: function(datas) {
      songs=[];
        for (var i = 0; i < datas.music_id.length; i++){
        var  song = {};
        song['imgSrc']=datas.pic_file[i];
        song['songName']=datas.music_name[i];
        song['id']=datas.music_id[i];
        song['user']=datas.uploader_name[i];
        song['playNum']=datas.play_times[i];
          song['likeNum'] = datas.like_times[i];
          songs.push(song);
      }
  $('#rankItems').hide();
  $('#rankItems').empty();


  var rankBoard = JSON.parse(sessionStorage.getItem('rankBoard'));
  for (rankItem of rankBoard) {
    if (rankItem['id'] === type) {
      $('#ranktitle').append('<h3 class="title">' + rankItem['name'] + '</h3>')
      $('#ranktitle').append('<a href="javascript:void(0);" onclick="addRankItem();"><i class="fa fa-arrow-left"></i></a>')
      break;
    }
  }

  $('#rankItems').append('<div class="musicEntry"><h3 class="musicItem">封面</h3><h3 class="musicItem">作品名</h3>' +
    '<h3 class="musicItem">创作者</h3><h3 class="musicItem">播放量</h3><h3 class="musicItem">点赞量</h3></div>');
  for (song of songs) {
    $('#rankItems').append('<div class="musicEntry"><img src="' + song['imgSrc'] + '" class="accompanyImage" />' +
      '<h4 class="accompanyInfo">' + song['songName'] + '</h4><h4 class="accompanyInfo">' + song['user'] + '</h4><h4 class="accompanyInfo">' + song['playNum'] + '</h4>' +
      '<h4 class="lastAccompanyInfo">' + song['likeNum'] + '</h4><a href="music.html?music_id='+song['id']+'" style="display: none;margin-right: 10px;"><i class="fa fa-play"></i></a></div>');
  }

  $('#rankItems').slideDown('slow');
  $('.musicEntry').hover(function() {
    $(this).children('.accompanyInfo').addClass('hoveron');
    $(this).children('.lastAccompanyInfo').addClass('hoveron');
    $(this).find('a').show();
  }, function() {
    $(this).children('.accompanyInfo').removeClass('hoveron');
    $(this).children('.lastAccompanyInfo').removeClass('hoveron');
    $(this).find('a').hide();
  })
  }
  });
}

function recommend(type) {
  var tags = type.split(' ');

  //on respond
  $('#' + tags[0] + 'Item').hide();
  $('#' + tags[0] + 'Item').empty();

  var songs = [{
      'imgSrc': 'images/test.jpg',
      'songName': 'Save me',
      'user': 'Hard Plan',
      'playNum': 1024,
      'likeNum': 512,
    },
    {
      'imgSrc': 'images/test1.jpg',
      'songName': 'Save us',
      'user': 'Demanding Plan',
      'playNum': 2048,
      'likeNum': 1024,
    },
    {
      'imgSrc': 'images/test2.jpg',
      'songName': 'Fuck me',
      'user': 'Hard Plan',
      'playNum': 512,
      'likeNum': 256,
    }
  ];

  var recomBoard = JSON.parse(sessionStorage.getItem(tags[0] + 'Board'));
  for (recomItem of recomBoard) {
    if (recomItem['id'] === type) {
      $('#' + tags[0] + 'title').append('<h3 class="title">' + recomItem['name'] + '</h3>')
      $('#' + tags[0] + 'title').append('<a href="javascript:void(0);" onclick="$(\'#' + tags[0] + 'Item\').slick(\'unslick\');add' + tags[0] + 'Item();"><i class="fa fa-arrow-left"></i></a>')
      break;
    }
  }

  $('#' + tags[0] + 'Item').append('<div class="musicEntry"><h3 class="musicItem">封面</h3><h3 class="musicItem">作品名</h3>' +
    '<h3 class="musicItem">创作者</h3><h3 class="musicItem">播放量</h3><h3 class="musicItem">点赞量</h3></div>');
  for (song of songs) {
    $('#' + tags[0] + 'Item').append('<div class="musicEntry"><img src="' + song['imgSrc'] + '" class="accompanyImage" />' +
      '<h4 class="accompanyInfo">' + song['songName'] + '</h4><h4 class="accompanyInfo">' + song['user'] + '</h4><h4 class="accompanyInfo">' + song['playNum'] + '</h4>' +
      '<h4 class="lastAccompanyInfo">' + song['likeNum'] + '</h4><a href="music.html?music_id='+song['id']+'" style="display: none;margin-right: 10px;"><i class="fa fa-play"></i></a></div>');
  }

  $('#' + tags[0] + 'Item').slideDown('slow');
  $('.musicEntry').hover(function() {
    $(this).children('.accompanyInfo').addClass('hoveron');
    $(this).children('.lastAccompanyInfo').addClass('hoveron');
    $(this).find('a').show();
  }, function() {
    $(this).children('.accompanyInfo').removeClass('hoveron')
    $(this).children('.lastAccompanyInfo').removeClass('hoveron');
    $(this).find('a').hide();
  })
}

