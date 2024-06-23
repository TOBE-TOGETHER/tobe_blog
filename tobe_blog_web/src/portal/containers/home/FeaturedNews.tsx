import { Button, Grid, Paper, Tab, Tabs, Typography } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { EContentType } from "../../../global/enums";
import { NewsDTO } from "../../../global/types";
import { PublicDataService } from "../../../services";
import NewsListItem from "./NewsListItem";
enum LoadType {
  Append,
  Replace,
}

export default function FeaturedArticles(props: {
  tags: string[];
  ownerId: string;
  contentType: EContentType;
  availableContentTypes: EContentType[];
  handleContentTypeChange: (newValue: EContentType) => void;
}) {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [newsData, setNewsData] = useState<NewsDTO[]>([]);
  const [current, setCurrent] = useState<number>(1);
  const [totalPage, setTotalPage] = useState<number>(1);

  const loadNews = useCallback(
    (
      _contentType: EContentType,
      _loadType: LoadType,
      _currentPage: number,
      _tags: string[],
      _newsData: NewsDTO[],
      _ownerId: string
    ): void => {
      PublicDataService.getNewsByTags(
        _contentType,
        10,
        _currentPage,
        _tags,
        _ownerId
      )
        .then((response) => {
          if (_loadType === LoadType.Append) {
            setNewsData(_newsData.concat(response.data.records));
          } else {
            setNewsData(response.data.records);
          }
          setCurrent(response.data.current);
          setTotalPage(response.data.pages);
        })
        .catch(() => {});
    },
    []
  );

  function getURIbyDomain(contentType: EContentType | string): string {
    switch (contentType) {
      case EContentType.Vocabulary:
        return "vocabularies";
      default:
        return contentType.toLowerCase() + "s";
    }
  }

  // based on current filters and load more data
  const handleLoadMoreRecords = (): void => {
    loadNews(
      props.contentType,
      LoadType.Append,
      current + 1,
      props.tags,
      newsData,
      props.ownerId
    );
  };

  useEffect(() => {
    // reset filter and load the first page data
    const handleTagFilterChange = (): void => {
      loadNews(
        props.contentType,
        LoadType.Replace,
        1,
        props.tags,
        newsData,
        props.ownerId
      );
    };
    handleTagFilterChange();
  }, [props.contentType, props.tags, loadNews]); // eslint-disable-line

  return (
    <Grid
      container
      component={Paper}
      sx={{ p: 0, width: "100%" }}
      variant="outlined"
    >
      <Tabs
        value={props.contentType}
        onChange={(_: React.SyntheticEvent, newValue: EContentType) =>
          props.handleContentTypeChange(newValue)
        }
        sx={{
          width: "100%",
          borderBottom: "1px solid rgba(0,0,0,0.12)",
          "& .MuiTab-root": {
            fontSize: "1rem",
            fontWeight: 400,
            py: 1.5,
            lineHeight: 1.75,
          },
        }}
        variant="fullWidth"
        textColor="secondary"
        indicatorColor="secondary"
      >
        {props.availableContentTypes.includes(EContentType.Article) && (
          <Tab value={EContentType.Article} label={t("home-page.articles")} />
        )}
        {props.availableContentTypes.includes(EContentType.Plan) && (
          <Tab value={EContentType.Plan} label={t("home-page.plans")} />
        )}
        {props.availableContentTypes.includes(EContentType.Vocabulary) && (
          <Tab value={EContentType.Vocabulary} label={t("home-page.vocabularies")} />
        )}
        {props.availableContentTypes.includes(EContentType.Vocabulary) && (
          <Tab value={EContentType.Collection} label={t("home-page.collections")} />
        )}
      </Tabs>
      {newsData.length > 0 ? (
        <>
          {newsData.map((n) => (
            <NewsListItem
              key={n.id}
              owner={n.ownerName}
              ownerId={n.ownerId}
              title={n.title}
              description={n.description}
              publishTime={n.publishTime}
              viewCount={n.viewCount}
              tags={n.tags}
              onClick={() =>
                navigate(`/news/${getURIbyDomain(n.contentType)}/${n.id}`)
              }
            />
          ))}
          {current >= totalPage ? (
            <Grid container item xs={12} justifyContent="center" sx={{ my: 1 }}>
              <Typography color="text.secondary" variant="body2">
                {t("home-page.end-line")}
              </Typography>
            </Grid>
          ) : (
            <Grid container item xs={12} justifyContent="center" sx={{ my: 1 }}>
              <Button variant="text" onClick={handleLoadMoreRecords}>
                {t("home-page.load-more")}
              </Button>
            </Grid>
          )}
        </>
      ) : (
        <Grid
          container
          item
          xs={12}
          justifyContent="center"
          alignContent="center"
          sx={{ my: 1, minHeight: "100px" }}
        >
          <Typography color="text.secondary" variant="body2">
            {t("home-page.no-content")}
          </Typography>
        </Grid>
      )}
    </Grid>
  );
}