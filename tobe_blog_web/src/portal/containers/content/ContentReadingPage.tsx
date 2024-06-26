import { Container, Grid } from '@mui/material';
import { ReactNode } from 'react';
import { IBaseUserContentDTO } from '../../../global/types';
import ContentBanner from '../../components/ContentBanner';
import ContentPageMetaBar from '../../components/ContentPageMetaBar';
import FrontendLayout from '../../components/FrontendLayout';

export function ContentReadingPage(props: { content: IBaseUserContentDTO | null; subTitle?: string; editLinkUrlPrefix: string; children: ReactNode }) {
  return (
    <FrontendLayout>
      {props.content && (
        <ContentBanner
          title={props.content.title}
          subTitle={props.subTitle}
          coverImgUrl={props.content.coverImgUrl}
          tags={props.content.tags}
        />
      )}
      <Container
        sx={{
          minHeight: '50vh',
          pb: 2,
        }}
      >
        <Grid container>
          {props.content && (
            <ContentPageMetaBar
              ownerId={props.content.ownerId}
              authorName={props.content.ownerName}
              publishTime={props.content.publishTime}
              viewCount={props.content.viewCount}
              editLinkUrl={props.editLinkUrlPrefix.replace(':id', props.content.id)}
            />
          )}

          {props.content && (
            <Grid
              item
              container
            >
              {props.children}
            </Grid>
          )}
        </Grid>
      </Container>
    </FrontendLayout>
  );
}
